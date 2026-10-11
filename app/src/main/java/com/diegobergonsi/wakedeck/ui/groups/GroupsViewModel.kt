package com.diegobergonsi.wakedeck.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegobergonsi.wakedeck.data.DeviceRepository
import com.diegobergonsi.wakedeck.data.GroupRepository
import com.diegobergonsi.wakedeck.domain.Device
import com.diegobergonsi.wakedeck.domain.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupRow(val group: Group, val deviceCount: Int)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groups: GroupRepository,
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = deviceRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val rows: StateFlow<List<GroupRow>> = combine(groups.observeAll(), deviceRepository.observeAll()) { groupList, all ->
        groupList.map { g -> GroupRow(g, all.count { it.groupId == g.id }) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Blank names are ignored. Names are trimmed. */
    fun save(id: Long, name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        viewModelScope.launch { groups.save(Group(id, clean)) }
    }

    /** Sets exactly which devices are in the group. A device is in one group at most, so chosen ones are moved here. */
    fun setMembers(groupId: Long, selected: Set<Long>) {
        viewModelScope.launch {
            val current = devices.value.filter { it.groupId == groupId }.map { it.id }.toSet()
            deviceRepository.setGroup((current - selected).toList(), null)
            deviceRepository.setGroup((selected - current).toList(), groupId)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { groups.delete(id) }
    }
}

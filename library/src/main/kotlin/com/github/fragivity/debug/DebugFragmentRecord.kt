package com.github.fragivity.debug

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.openActiveFragments

internal data class DebugFragmentRecord(
    val fragmentName: CharSequence,
    val childrenList: List<DebugFragmentRecord>
)

internal fun FragmentActivity.getFragmentRecords(
    predicate: (Fragment?) -> Boolean = { true }
): List<DebugFragmentRecord> {
    val fragmentList = supportFragmentManager.openActiveFragments
    if (fragmentList.isNullOrEmpty()) return emptyList()

    val fragmentRecordList = mutableListOf<DebugFragmentRecord>()
    fragmentList.forEach { fragment ->
        if (predicate(fragment)) {
            addDebugFragmentRecord(fragmentRecordList, fragment, predicate)
        }
    }
    return fragmentRecordList
}

private fun addDebugFragmentRecord(
    fragmentRecords: MutableList<DebugFragmentRecord>,
    fragment: Fragment?,
    predicate: (Fragment) -> Boolean
) {
    if (fragment == null) return

    val name = "${fragment::class.java.simpleName}@" +
        System.identityHashCode(fragment).toString(16)
    fragmentRecords.add(DebugFragmentRecord(name, getChildFragmentRecords(fragment, predicate)))
}

private fun getChildFragmentRecords(
    parentFragment: Fragment,
    predicate: (Fragment) -> Boolean
): List<DebugFragmentRecord> {
    // java.lang.IllegalStateException: Fragment .. has not been attached yet.
    if (!parentFragment.isAdded) return emptyList()

    val fragmentList = parentFragment.childFragmentManager.openActiveFragments
    if (fragmentList.isNullOrEmpty()) return emptyList()

    val fragmentRecordList = mutableListOf<DebugFragmentRecord>()
    fragmentList.forEach { fragment ->
        if (predicate(fragment)) {
            addDebugFragmentRecord(fragmentRecordList, fragment, predicate)
        }
    }
    return fragmentRecordList
}
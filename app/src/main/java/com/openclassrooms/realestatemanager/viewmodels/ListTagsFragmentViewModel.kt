package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.ViewModel

/**
 * View Model class containing the fragments order in backstack (list of associated fragment tags).
 */
class ListTagsFragmentViewModel: ViewModel() {

    var listTags: MutableList<String> = mutableListOf()

    fun addFragmentTagToList(tag: String) = listTags.add(tag)

    fun removeTagToList(tag: String) = listTags.remove(tag)
}
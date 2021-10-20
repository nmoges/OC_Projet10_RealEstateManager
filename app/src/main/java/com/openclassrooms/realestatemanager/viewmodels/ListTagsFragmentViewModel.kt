package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.ViewModel

/**
 * View Model class containing the fragments order in backstack (list of associated fragment tags).
 */
class ListTagsFragmentViewModel: ViewModel() {

    var listTags: MutableList<String> = mutableListOf()

    /**
     * Adds tag to [listTags].
     * @param tag : tag to add
     */
    fun addFragmentTagToList(tag: String) = listTags.add(tag)

    /**
     * Removes tag from [listTags].
     * @param tag : tag to remove
     */
    fun removeTagToList(tag: String) = listTags.remove(tag)
}
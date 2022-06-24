/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.*
import com.google.samples.apps.sunflower.PlantListFragment
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for [PlantListFragment].
 */
@HiltViewModel
class PlantListViewModel @Inject internal constructor(
    plantRepository: PlantRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Encapsulate both filters in a single class; this way, we can listen to any change on either
    // Implementation decision: only one filter is active at a time, when we apply one, we reset the other
    private val filters: MutableStateFlow<Filter> = MutableStateFlow(
        Filter(savedStateHandle.get(GROW_ZONE_SAVED_STATE_KEY) ?: NO_GROW_ZONE,
            savedStateHandle.get(NAME_FILTER_SAVED_STATE_KEY) ?: ""))

    val plants: LiveData<List<Plant>> = filters.flatMapLatest { filter ->
        if (filter.growZone != NO_GROW_ZONE) {
            plantRepository.getPlantsWithGrowZoneNumber(filter.growZone)
        } else if (filter.name.isNotEmpty()) {
            plantRepository.getPlantsByName(filter.name)
        } else {
            plantRepository.getPlants()
        }
    }.asLiveData()

    init {

        /**
         * When `growZone` changes, store the new value in `savedStateHandle`.
         *
         * There are a few ways to write this; all of these are equivalent. (This info is from
         * https://github.com/android/sunflower/pull/671#pullrequestreview-548900174)
         *
         * 1) A verbose version:
         *
         *    viewModelScope.launch {
         *        growZone.onEach { newGrowZone ->
         *            savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *        }
         *    }.collect()
         *
         * 2) A simpler version of 1). Since we're calling `collect`, we can consume
         *    the elements in the `collect`'s lambda block instead of using the `onEach` operator.
         *    This is the version that's used in the live code below.
         *
         * 3) We can avoid creating a new coroutine using the `launchIn` terminal operator. In this
         *    case, `onEach` is needed because `launchIn` doesn't take a lambda to consume the new
         *    element in the Flow; it takes a `CoroutineScope` that's used to create a coroutine
         *    internally.
         *
         *    growZone.onEach { newGrowZone ->
         *        savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *    }.launchIn(viewModelScope)
         */
        viewModelScope.launch {
            filters.collect { newFilter ->
                savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newFilter.growZone)
                savedStateHandle.set(NAME_FILTER_SAVED_STATE_KEY, newFilter.name)
            }
        }
    }

    fun setGrowZoneNumber(num: Int) {
        filters.value = Filter(num, "")
    }

    fun clearGrowZoneNumber() {
        filters.value = Filter(NO_GROW_ZONE, "")
    }

    fun isFiltered() = filters.value.growZone != NO_GROW_ZONE

    fun filterByName(name: String) {
        filters.value = Filter(NO_GROW_ZONE, name)
    }

    companion object {
        private const val NO_GROW_ZONE = -1
        private const val GROW_ZONE_SAVED_STATE_KEY = "GROW_ZONE_SAVED_STATE_KEY"
        private const val NAME_FILTER_SAVED_STATE_KEY = "NAME_FILTER_SAVED_STATE_KEY"
    }

    // encapsulate both filters in a single class; this way, we can listen to any change on either
    class Filter(
        val growZone: Int,
        val name: String
    )
}

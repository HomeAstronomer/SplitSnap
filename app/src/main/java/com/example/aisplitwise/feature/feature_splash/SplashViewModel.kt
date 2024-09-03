package com.example.aisplitwise.feature.feature_splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aisplitwise.data.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
): ViewModel() {
    var isLoggedIn=false
    init {
        viewModelScope.launch (Dispatchers.IO){
            memberRepository.getMemberDb().collect{member->
                member.getOrNull(0)?.let{
                    isLoggedIn=true
                }
            }
        }
    }

}
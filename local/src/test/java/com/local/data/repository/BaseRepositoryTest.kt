package com.local.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.local.data.local.ExpenseDao
import com.local.data.local.GroupDao
import com.local.data.local.MemberDao
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class BaseRepositoryTest {

    @Mock
    protected lateinit var groupDao: GroupDao

    @Mock
    protected lateinit var memberDao: MemberDao

    @Mock
    protected lateinit var expenseDao: ExpenseDao

    @Mock
    protected lateinit var fireStoreDb: FirebaseFirestore
}
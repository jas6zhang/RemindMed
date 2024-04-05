package com.example.remindmed

import com.gradle.controller.DoctorController
import com.gradle.models.Doctor
import com.gradle.ui.views.DoctorViewModel
import com.gradle.ui.views.shared.ProfileViewEvent
import org.junit.Test
import org.junit.Assert.*


internal class DoctorProfileUnitTests {
    private val testDoc : Doctor = Doctor("1", "Gen", "gen@gmail.com")

    @Test
    fun viewModelTests() {
        // changing the various fields
        var viewModDoc : DoctorViewModel = DoctorViewModel(testDoc)

        assertEquals(viewModDoc.name.value, testDoc.name)
        assertEquals(viewModDoc.email.value, testDoc.email)
        assertEquals(viewModDoc.successfulChange.value, testDoc.successfulChange)
        assertEquals(viewModDoc.submitEnabled.value, testDoc.submitEnabled)

        testDoc.name = "Ben"
        assertEquals("Ben", viewModDoc.name.value)
        testDoc.email = "ben@gmail.com"
        assertEquals("ben@gmail.com", viewModDoc.email.value)
    }

    @Test
    fun controllerTests() {
        val controller = DoctorController(testDoc)

        controller.invoke(ProfileViewEvent.NameEvent, "Jacob")
        assertEquals("Jacob", testDoc.name)

        controller.invoke(ProfileViewEvent.EmailEvent, "not an email")
        assertEquals(false, testDoc.submitEnabled)

        controller.invoke(ProfileViewEvent.DismissEvent, "")
        assertEquals(false, testDoc.changesSubmitted)
    }
}
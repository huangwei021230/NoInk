package com.bagel.noink
import com.bagel.noink.activity.RegisterActivity
import com.bagel.noink.utils.InformationCalc
import org.junit.Test

import org.junit.Assert.*

class InformationCalcTest {
    @Test
    fun calculateAgeTest1() {
        val result = InformationCalc.calculateAge("2003.08.18")
        assertEquals(20, result);
    }

    @Test
    fun calculateAgeTest2() {
        val result = InformationCalc.calculateAge("2003.8.18")
        assertEquals(20, result);
    }

//    @Test
//    fun calculateAgeTest2() {
//        val result = InformationCalc.calculateAge("2003.12.2")
//        assertEquals(20, result);
//    }
//
//    @Test
//    fun calculateAgeTest3() {
//        val result = InformationCalc.calculateAge("2003.12.3")
//        assertEquals(19, result);
//    }

    @Test
    fun convertDateFormatTest() {
        val result = InformationCalc.convertDateFormat("2003.8.18");
        assertEquals("2003-8-18", result);
    }
}
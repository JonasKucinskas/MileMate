package com.example.milemate

import android.icu.util.Calendar

class Reminder constructor(ReminderDate: Calendar, CheckUpDate: Calendar) {

        var reminderDate = ReminderDate.time
        var checkUpDate = CheckUpDate.time
}
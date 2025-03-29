package com.spksh.todoline.ui.model

import java.time.LocalDate

interface TimeAware {
    fun getDay(): LocalDate?
}
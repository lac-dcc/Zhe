package com.mscufmg.Zhe.logsim.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer {
    private DateTimeFormatter dtf;

    public Timer(String format){
        this.dtf = DateTimeFormatter.ofPattern(format);
    }

    public String now(){
        LocalDateTime now = LocalDateTime.now();
        return this.dtf.format(now);
    }
}

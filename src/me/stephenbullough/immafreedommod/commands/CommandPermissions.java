package me.stephenbullough.immafreedommod.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermissions
{
    StaffLevel level();

    SourceType source();

    boolean blockHostConsole() default false;
}

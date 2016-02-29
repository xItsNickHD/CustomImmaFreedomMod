package me.stephenbullough.immafreedommod.commands;

public enum StaffLevel
{
    ALL("All Player Commands"), OP("OP Commands"), STAFF1("SuperAdmin Commands"), STAFF2("Senior Admin Commands");
    //
    private final String friendlyName;

    private StaffLevel(String friendlyName)
    {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName()
    {
        return friendlyName;
    }
}

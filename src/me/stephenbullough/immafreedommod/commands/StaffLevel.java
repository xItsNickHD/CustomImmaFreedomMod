package me.stephenbullough.immafreedommod.commands;

public enum StaffLevel
{
    ALL("All Player Commands"), DEFAULT("DEFAULT Commands"), HELPER("HELPER Commands"), MOD("MOD Commands"), ADMIN("ADMIN Commands"), EXEC("EXECUTIVE Commands");
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

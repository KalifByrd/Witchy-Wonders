package com.toxicteddie.witchywonders.factions;


import java.util.HashSet;
import java.util.Set;

public class Faction implements IFaction{
    private FactionType faction = FactionType.HUMAN; // default
    private Set<String> members;

    public Faction()
    {
        this.members = new HashSet<>();
    }

    @Override
    public void setFaction(FactionType faction)
    {
        this.faction = faction;
    }

    @Override
    public FactionType getFaction()
    {
        return faction;
    }

    @Override
    public void addMember(String playerName)
    {
        members.add(playerName);
    }

    @Override
    public void removeMember(String playerName)
    {
        members.remove(playerName);
    }

    @Override
    public boolean isMember(String playerName)
    {
        return members.contains(playerName);
    }

    @Override
    public Set<String> getMembers() {
        return new HashSet<>(members); // Return a copy to prevent modification outside
    }

    
}
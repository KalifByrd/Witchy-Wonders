package com.toxicteddie.witchywonders.factions;

import java.util.Set;

public interface IFaction
{
    enum FactionType
    {
        HUMAN, WITCH
    }
    void setFaction(FactionType faction);
    FactionType getFaction();

    void addMember(String playerName);
    void removeMember(String playerName);
    boolean isMember(String playerName);

    Set<String> getMembers();
    
}
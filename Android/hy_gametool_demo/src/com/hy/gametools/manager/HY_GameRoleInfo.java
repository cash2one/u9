package com.hy.gametools.manager;


/*******************************************
 * @CLASS:HY_GameRoleInfo
 * @AUTHOR:smile
 *******************************************/
public class HY_GameRoleInfo 
{
    public int typeId;   //    typeId 当前情景，目前支持 enterServer，levelUp，createRole
    public String roleId;//    roleId 当前登录的玩家角色ID，必须为数字，若无，传入userid
    public String roleName;//    roleName 当前登录的玩家角色名，不能为空，不能为null，若无，传入"游戏名称+username"，如"刀塔传奇风吹来的鱼"
    public String roleLevel;//    roleLevel 当前登录的玩家角色等级，必须为数字，若无，传入1
    public String zoneId;//    zoneId 当前登录的游戏区服ID，必须为数字，若无，传入1
    public String zoneName;//    zoneName 当前登录的游戏区服名称，不能为空，不能为null，若无，传入游戏名称+"1区"，如"刀塔传奇1区"
    public int balance;//    balance 当前用户游戏币余额，必须为数字，若无，传入0
    public String vip;//    vip 当前用户VIP等级，必须为数字，若无，传入1
    public String partyName;//    partyName 当前用户所属帮派，不能为空，不能为null，若无，传入"无帮派"
    
  
    public int getTypeId()
    {
        return typeId;
    }
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }
    public String getRoleId()
    {
        return roleId;
    }
    public void setRoleId(String roleId)
    {
        this.roleId = roleId;
    }
    public String getRoleName()
    {
        return roleName;
    }
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
    public String getRoleLevel()
    {
        return roleLevel;
    }
    public void setRoleLevel(String roleLevel)
    {
        this.roleLevel = roleLevel;
    }
    public String getZoneId()
    {
        return zoneId;
    }
    public void setZoneId(String zoneId)
    {
        this.zoneId = zoneId;
    }
    public String getZoneName()
    {
        return zoneName;
    }
    public void setZoneName(String zoneName)
    {
        this.zoneName = zoneName;
    }
    public int getBalance()
    {
        return balance;
    }
    public void setBalance(int balance)
    {
        this.balance = balance;
    }
    public String getVip()
    {
        return vip;
    }
    public void setVip(String vip)
    {
        this.vip = vip;
    }
    public String getPartyName()
    {
        return partyName;
    }
    public void setPartyName(String partyName)
    {
        this.partyName = partyName;
    }
    @Override
    public String toString()
    {
        return "HY_GameRoleInfo [typeId=" + typeId + ", roleId=" + roleId + ", roleName="
                + roleName + ", roleLevel=" + roleLevel + ", zoneId=" + zoneId
                + ", zoneName=" + zoneName + ", balance=" + balance + ", vip="
                + vip + ", partyName=" + partyName + "]";
    }
    

//    public HY_GameRoleInfo(int typeId, String roleId, String roleName,
//            int roleLevel, String zoneId,String zoneName,int balance,
//            int vip,String partyName)
//    {
//        super();
//        this.typeId = typeId;
//        this.roleId = roleId;
//        this.roleName = roleName;
//        this.roleLevel = roleLevel;
//        this.zoneId = zoneId;
//        this.zoneName = zoneName;
//        this.balance = balance;
//        this.vip = vip;
//        this.partyName = partyName;
//    }

    
}


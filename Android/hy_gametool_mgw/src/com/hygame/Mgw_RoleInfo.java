package com.hygame;

public class Mgw_RoleInfo{
	public static int typeId;   //    typeId 当前情景，目前支持 enterServer，levelUp，createRole
	public static String roleId;//    roleId 当前登录的玩家角色ID，必须为数字，若无，传入userid
	public static String roleName;//    roleName 当前登录的玩家角色名，不能为空，不能为null，若无，传入"游戏名称+username"，如"刀塔传奇风吹来的鱼"
	public static String roleLevel;//    roleLevel 当前登录的玩家角色等级，必须为数字，若无，传入1
	public static String zoneId;//    zoneId 当前登录的游戏区服ID，必须为数字，若无，传入1
	public static String zoneName;//    zoneName 当前登录的游戏区服名称，不能为空，不能为null，若无，传入游戏名称+"1区"，如"刀塔传奇1区"
	public static int balance;//    balance 当前用户游戏币余额，必须为数字，若无，传入0
	public static String vip;//    vip 当前用户VIP等级，必须为数字，若无，传入1
	public static String partyName;//    partyName 当前用户所属帮派，不能为空，不能为null，若无，传入"无帮派"
}
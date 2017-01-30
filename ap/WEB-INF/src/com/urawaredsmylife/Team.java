package com.urawaredsmylife;

/**
 * チーム情報を持つクラス
 * @author motoy3d
 */
public class Team {
	private String teamId;
	private String teamName;
	public Team() {
	}
	public Team(String teamId, String teamName) {
		this.teamId = teamId;
		this.teamName = teamName;
	}
	public String getTeamId() {
		return teamId;
	}
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
}

package com.next.config;

public class SHVersion {

	private int mainVersion = 0;// 主版本
	private int secondVersion = 0;// 次版本
	private int reviseVersion = 0;// 修正版本

	public int getSecondVersion() {
		return secondVersion;
	}

	public int getMainVersion() {
		return mainVersion;
	}

	public int getReviseVersion() {
		return reviseVersion;
	}

	public Boolean isNewer(SHVersion version) {
		if (mainVersion > version.mainVersion) {
			return true;
		} else if ( mainVersion == version.mainVersion &&  secondVersion > version.secondVersion) {
			return true;
		} else if ( mainVersion == version.mainVersion && secondVersion == version.secondVersion && reviseVersion > version.reviseVersion) {
			return true;
		}
		return false;
	}

	public SHVersion(String version) {
		String[] ver = version.split("\\.");
		if (ver.length == 3) {
			mainVersion = Integer.parseInt(ver[0]);
			secondVersion = Integer.parseInt(ver[1]);
			reviseVersion = Integer.parseInt(ver[2]);
		}
	}
	
	@Override
	public String toString() {
		return mainVersion + "." + secondVersion + "." + reviseVersion;
	}
}

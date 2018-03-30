package com.union.interfaces;

import java.util.concurrent.atomic.AtomicInteger;

import com.union.config.ConfigParams;

/**
 * 服务器
 * 
 * @author wu
 * @date 2016-04-13
 * @version 1.0
 */
public class UnionServer extends BaseServer {
	
	private String type;  // 服务器类型: "master"表示主服务器; "backup"表示备份服务器
	private String switchState;  // 开关状态: "up"为启用; "down"为停用; "recovering";  //恢复中
	
	private long downTime; //服务器置为不可用时的时间戳
	private UnionServerGroup belongGroup;  // 服务器所属组
	
	// 负载均衡-最少任务数
	private final AtomicInteger atomicTask = new AtomicInteger();

	// 服务器检测标识
	private volatile boolean checkFlag = false;
	
	public int getTaskCounts() {
		return atomicTask.get();
	}
	
	public void addTaskCounts() {
		atomicTask.getAndIncrement();
	}
	
	public void reduceTaskCounts() {
		atomicTask.getAndDecrement();
	}
	
	public UnionServer(String ip, int port, String type, String switchState) {
		super(ip, port, true);
		this.type = type;
		this.switchState = switchState;
	}
	
	
	@Override
	public boolean getAliveState() {
		return super.getAliveState() && ConfigParams.SERVER_SWITCH_ON.equalsIgnoreCase(switchState);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSwitchState() {
		return switchState;
	}

	public void setSwitchState(String switchState) {
		this.switchState = switchState;
	}

	public UnionServerGroup getBelongGroup() {
		return belongGroup;
	}

	public void setBelongGroup(UnionServerGroup belongGroup) {
		this.belongGroup = belongGroup;
	}

	public boolean getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(boolean checkFlag) {
		this.checkFlag = checkFlag;
	}

	public long getDownTime() {
		return downTime;
	}

	public void setDownTime(long downTime) {
		this.downTime = downTime;
	}

}

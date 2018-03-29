package com.union.interfaces;

/**
 * 服务器分配接口
 * 
 * @author wu
 * @date 2016-04-15
 * @version 1.0
 * 
 * @param <T> 服务器组实体类
 * @param <S> 服务器实体类
 */
public interface ServerAssignerI<T extends ServerGroupI<S>, S extends ServerI> {
	
	/**
	 * 从服务器组中分配一个服务器
	 * 
	 * @param serverGroup 服务器组
	 * 
	 * @return ServerI 服务器
	 */
	public S assignOneServer(T serverGroup);

}

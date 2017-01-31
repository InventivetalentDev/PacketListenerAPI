package org.inventivetalent.packetlistener.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) public @interface PacketOptions {

	/**
	 * @return <code>true</code> if packets should be limited to Player packets, <code>false</code> otherwise
	 */
	boolean forcePlayer() default false;

	/**
	 * @return <code>true</code> if packets should be limited to Server packets, <code>false</code> otherwise
	 */
	boolean forceServer() default false;

}

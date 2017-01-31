package de.inventivegames.packetlistener.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Please use {@link org.inventivetalent.packetlistener.handler.PacketOptions}
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) @Deprecated public @interface PacketOptions {

	/**
	 * @return <code>true</code> if packets should be limited to Player packets, <code>false</code> otherwise
	 */
	@Deprecated boolean forcePlayer() default false;

	/**
	 * @return <code>true</code> if packets should be limited to Server packets, <code>false</code> otherwise
	 */
	@Deprecated boolean forceServer() default false;

}

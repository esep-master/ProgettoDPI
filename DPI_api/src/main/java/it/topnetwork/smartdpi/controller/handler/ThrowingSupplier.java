package it.topnetwork.smartdpi.controller.handler;

@FunctionalInterface
public interface ThrowingSupplier<T> {
	T get() throws Exception;
}

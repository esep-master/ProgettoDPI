package it.topnetwork.smartdpi.controller.handler;

@FunctionalInterface
public interface ThrowingSupplierVoid {
	void get() throws Exception;
}

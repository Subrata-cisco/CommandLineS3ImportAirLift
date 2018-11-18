package com.sp.dataimport.cli;

import java.io.IOException;

public abstract class AbstractCommand implements Runnable {

	@Override
	public final void run() {
		try {
			invoke();
		} catch (IOException e) {
			System.out.println("CLI processing resulted in an exception: {0}"
					+ e);
		}
	}

	protected abstract void invoke() throws IOException;
}

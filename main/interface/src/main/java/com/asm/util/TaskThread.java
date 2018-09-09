package com.asm.util;


class TaskThread<T> extends Thread
{
	public Task<T> task;
	
	
	public TaskThread(Task<T> task) {
		this.task = task;
	}
	
	@Override
	public void run() {
		task.run();

	}
}

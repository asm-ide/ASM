package com.asm.coreui.activity;

import com.asm.io.RemoteStream;


interface ITestService
{
	void work(in String name, in RemoteStream binder);
}

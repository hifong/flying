package com.flying.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CallableDemo {

	static class ReturnThread implements Callable<Integer> {

		// 实现call方法，作为线程执行体
		@Override
		public Integer call() {
			int i = 0;
			for (; i < 100; i++) {
				System.out.println(Thread.currentThread().getName() + " 循环变量i的值：" + i);
			}
			return i;
		}
	}

	public static void main(String[] args) {

		// 创建Callable对象
		ReturnThread t = new ReturnThread();
		// 使用FutureTask包装Callable对象
		FutureTask<Integer> task = new FutureTask<Integer>(t);

		for (int i = 0; i < 100; i++) {
			System.out.println(Thread.currentThread().getName() + " 循环变量i的值：" + i);
			if (i == 20) {
				new Thread(task, "有返回值的线程").start();
			}
		}

		try {
			// 获取线程返回值
			System.out.println("子线程的返回值： " + task.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

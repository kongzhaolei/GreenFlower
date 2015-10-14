package org.gradle.needle.client;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class DllClient {

	// 编写dll接口,实现library接口
	public interface DataProviderServiceDll extends Library {
		DataProviderServiceDll instancedll = (DataProviderServiceDll) Native
				.loadLibrary("SoftAdapter",DataProviderServiceDll.class);
		
		public void StartService();
		public void GetErrorTypeByID(String id);
		
			
	}

	public static void main(String[] args) {
		DataProviderServiceDll.instancedll.GetErrorTypeByID("20103323");
	}

}

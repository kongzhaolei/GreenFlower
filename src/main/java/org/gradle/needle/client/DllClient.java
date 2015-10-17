/**
 * JNA和JNR只能调用非托管代码，对.NET的这种托管项目无可奈何
 * 因此需要另寻他路了
 */

package org.gradle.needle.client;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.exceptions.NativeException;

import jnr.ffi.LibraryLoader;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class DllClient {

	// 编写dll接口,实现library接口
	public interface SoftAdapterJNA extends Library {
		SoftAdapterJNA instancedll = (SoftAdapterJNA) Native.loadLibrary("SoftAdapter", SoftAdapterJNA.class);

		public void StartService();

		public void GetErrorTypeByID(String id);
	}

	public interface SoftAdapterJNR {
		public void GetErrorTypeByID(String id);
	}

	public static void SoftAdapterJNtive() throws NativeException, IllegalAccessException {
		System.setProperty("jnative.debug", "true");
		System.setProperty("jnative.loadNative", "S:\\workspace\\GreenFlower\\src\\main\\resources\\JNativeCpp.dll");
		System.loadLibrary("SoftAdapter");
		JNative jnative = new JNative("SoftAdapter", "GetErrorTypeByID");
		jnative.setParameter(0, "20103323");
		jnative.invoke();
		System.out.println(jnative.getRetVal());
	}

	public static void main(String[] args) {
		// SoftAdapterJNA.instancedll.GetErrorTypeByID("20103323");
		// SoftAdapterJNR libc =
		// LibraryLoader.create(SoftAdapterJNR.class).load("SoftAdapter");
		// libc.GetErrorTypeByID("20103323");
		System.out.println(System.getProperty("java.library.path"));
		try {
			DllClient.SoftAdapterJNtive();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NativeException e) {
			e.printStackTrace();
		}

	}

}

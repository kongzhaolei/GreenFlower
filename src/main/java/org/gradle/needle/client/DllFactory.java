/**
 * JNA��JNRֻ�ܵ��÷��йܴ��룬��.NET�������й���Ŀ�޿��κ�
 * �˺������޾���
 */

package org.gradle.needle.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;
import org.jawin.io.LittleEndianInputStream;
import org.jawin.io.LittleEndianOutputStream;
import org.jawin.io.NakedByteStream;
import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class DllFactory {

	// 1.JNA��ʽ����дdll�ӿ�,ʵ��library�ӿ�
	public interface SoftAdapterJNA extends Library {
		SoftAdapterJNA instancedll = (SoftAdapterJNA) Native.loadLibrary(
				"SoftAdapter", SoftAdapterJNA.class);

		public void StartService();

		public void DelErrorCode(String id);

		public String GetLastTenData(int devid, String iecpath);
	}

	// 2. JNR��ʽ��
	public interface SoftAdapterJNR {
		public void GetErrorTypeByID(String id);
	}

	// 3. JNative��ʽ��
	public static void SoftAdapterJNtive() {
		System.setProperty("jnative.debug", "true");
		// ������־��¼�Ŀ���,�������JNative�����й���
		JNative.setLoggingEnabled(true);
		// ���ض�̬���ӿ�
		System.loadLibrary("SoftAdapter");
		try {
			// ����̬���ӿ�����ֺͷ������ݸ�JNative
			JNative jnative = new JNative("SoftAdapter", "GetErrorTypeByID");
			// ���õ��ö�̬��ķ�����ķ�������
			jnative.setRetVal(Type.STRING);
			// ���ò���
			jnative.setParameter(0, "20103323");
			// ���÷���
			jnative.invoke();
			System.out.println(jnative.getRetVal());
		} catch (NativeException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// 4. jawin��ʽ��
	public static void SoftAdapterJawin() {
		try {
			FuncPtr jaw = new FuncPtr("SoftAdapter.dll", "DelErrorCode@4");
			String marshal = "II:I:L4L4";
			NakedByteStream nbs = new NakedByteStream();
			LittleEndianOutputStream leos = new LittleEndianOutputStream(nbs);
			leos.writeInt(4);
			leos.writeInt(3);
			byte[] res = jaw.invoke(marshal, 12, nbs, null,
					ReturnFlags.CHECK_W32);
			ByteArrayInputStream bais = new ByteArrayInputStream(res);
			LittleEndianInputStream leis = new LittleEndianInputStream(bais);
			int result = leis.readInt();
			System.out.println(result);

			// DispatchPtr app = new DispatchPtr("SoftAdapter");
			// app.invoke("DelErrorCode","001");

		} catch (COMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 5. jacob��ʽ��ͨ��com���
	public static void SoftAdapterJacob() {
		try {
			ActiveXComponent dotnetCom = null;
			dotnetCom = new ActiveXComponent(
					"GoldWind.DataProviderService.ControlPlatSupply.MonitorData.TenMinutes");
			Variant var = Dispatch.call(dotnetCom, "GetLastTenData",652325001,"[visu_yaw_left_feedback]");
			String str = var.toString();
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
		// SoftAdapterJNA.instancedll.DelErrorCode("01");
		// SoftAdapterJNA.instancedll.GetLastTenData(01,"11");
		// SoftAdapterJNR libc =
		// LibraryLoader.create(SoftAdapterJNR.class).load("SoftAdapter");
		// libc.GetErrorTypeByID("20103323");
		// DllClient.SoftAdapterJNtive();
		// DllClient.SoftAdapterJawin();
		DllFactory.SoftAdapterJacob();

	}
}

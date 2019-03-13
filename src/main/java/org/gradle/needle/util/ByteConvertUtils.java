package org.gradle.needle.util;

public class ByteConvertUtils {

	public static float[] getFloat(String[] s, boolean swap) {
		int il = s.length / 2;
		char ch1, ch2;
		byte[] dd0, dd1;
		float[] ro = new float[il];
		for (int i = 0; i < il; i++) {
			ch1 = (char) (Integer.valueOf(s[i * 2]) + 0);
			ch2 = (char) (Integer.valueOf(s[i * 2 + 1]) + 0);
			dd0 = ByteArrayConveter.getByteArray(ch1);
			dd1 = ByteArrayConveter.getByteArray(ch2);
			byte[] b = new byte[4];
			if (swap) {
				b[0] = dd0[0];
				b[1] = dd0[1];
				b[2] = dd1[0];
				b[3] = dd1[1];
			} else {
				b[0] = dd1[0];
				b[1] = dd1[1];
				b[2] = dd0[0];
				b[3] = dd0[1];
			}
			ro[i] = ByteArrayConveter.getFloat(b, 0);
		}
		return ro;
	}

	public static float getFloat(String[] s, int index, boolean swap) {
		char ch1, ch2;
		byte[] dd0, dd1;
		ch1 = (char) (Integer.valueOf(s[index]) + 0);
		ch2 = (char) (Integer.valueOf(s[index + 1]) + 0);
		dd0 = ByteArrayConveter.getByteArray(ch1);
		dd1 = ByteArrayConveter.getByteArray(ch2);
		byte[] b = new byte[4];
		if (swap) {
			b[0] = dd0[0];
			b[1] = dd0[1];
			b[2] = dd1[0];
			b[3] = dd1[1];
		} else {
			b[0] = dd1[0];
			b[1] = dd1[1];
			b[2] = dd0[0];
			b[3] = dd0[1];
		}
		return ByteArrayConveter.getFloat(b, 0);
	}

	public static Short getShort(String[] s, int index) {
		char ch1, ch2;
		byte[] dd0, dd1;
		ch1 = (char) (Integer.valueOf(s[index]) + 0);
		dd0 = ByteArrayConveter.getByteArray(ch1);
		byte[] b = new byte[2];
		b[0] = dd0[0];
		b[1] = dd0[1];
		return ByteArrayConveter.getShort(b, 0);
	}

	public static byte[] getBytes(String[] s) {
		byte[] r = new byte[s.length * 2];
		int il = s.length;
		char ch1;
		byte[] lb;
		for (int i = 0; i < il; i++) {
			ch1 = (char) (Integer.valueOf(s[i]) + 0);
			lb = ByteArrayConveter.getByteArray(ch1);
			r[i * 2] = lb[0];
			r[i * 2 + 1] = lb[1];
		}
		return r;
	}
	

	public static short[] getShort(Double val) {
		byte[] bvals = ByteArrayConveter.getByteArray(val);
		short[] datas = new short[2];
		datas[0] = ByteArrayConveter.getShort(bvals, 0);
		datas[1] = ByteArrayConveter.getShort(bvals, 2);
		return datas;
	}

	public static short[] getShort(Float val) {
		byte[] bvals = ByteArrayConveter.getByteArray(val);
		short[] datas = new short[2];
		datas[0] = ByteArrayConveter.getShort(bvals, 0);
		datas[1] = ByteArrayConveter.getShort(bvals, 2);
		return datas;
	}
	

}

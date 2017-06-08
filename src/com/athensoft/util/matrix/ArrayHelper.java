package com.athensoft.util.matrix;

public class ArrayHelper {
	
	public static void printArray(int[][] array){
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				System.out.print(array[i][j]+"\t");
			}
			System.out.println("\n");
		}
	}
	
	
	public static void printArray(String[][] array){
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				System.out.print(array[i][j]+"\t");
			}
			System.out.println("\n");
		}
	}
	
	public static <T> void printArray(T[][] array){
		for(int i=0; i<array.length; i++){
			for(int j=0; j<array[0].length; j++){
				System.out.print(array[i][j]+"\t");
			}
			System.out.println("\n");
		}
	}

	public static void main(String[] args) {
		String[][] data = new String[11][4];
		data[0] = new String[]{"","ϵͳȨ�޹���", "1","a","b"};
		data[1] = new String[]{"","�û�����", "ϵͳȨ�޹���","a","b"};
		data[2] = new String[]{"","�û�����", "ϵͳȨ�޹���","a","b"};
		data[3] = new String[]{"","�û�����", "ϵͳȨ�޹���","a","b"};
		data[4] = new String[]{"","�û��˵�����", "ϵͳȨ�޹���","a","b"};
		data[5] = new String[]{"","��ɫ�˵�����", "ϵͳȨ�޹���","a","b"};
		data[6] = new String[]{"","�û�Ȩ�޹���", "ϵͳȨ�޹���","a","b"};
		data[7] = new String[]{"","վ����", "1","a","b"};
		data[8] = new String[]{"","д��", "վ����","a","b"};
		data[9] = new String[]{"","����", "վ����","a","b"};
		data[10] = new String[]{"","�ݸ�", "վ����","a","b"};
		
		Integer[][] data1 = new Integer[3][3];
		data1[0] = new Integer[]{11,12,13};
		data1[1] = new Integer[]{21,22,23};
		data1[2] = new Integer[]{31,32,33};
		
		printArray(data);
		printArray(data1);
		

	}

}

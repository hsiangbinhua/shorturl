package com.ffcs.emp.st.util;

import java.util.Random;

/**********************************************************
 *
 * 文件名称： ShortLenUrlUtil.java
 * 摘    要： 短地址核心算法
 *
 * 初始版本：1.0.0.0
 * 作    者： 刘葳
 * 完成日期： Dec 16, 2011 8:59:05 PM
 * 
 ************************************************************/

public class ShortLenUrlUtil {

	public final int SEQ_CODE_LEN			= 0x6;

	private static int g_seq_code = 0;
	private static boolean g_init = false;
	
	private final static int SEQ_VALUE_BITS		= 0x6;
	private final static int SEQ_FLAG_BITS		= 0x4;
	private final static int SEQ_CODE_BITS		= 0x20;
	private final static int SEQ_CODE_FLAG_POS	= 0x4;
	private final static int SEQ_CODE_MASK		= 0x3f;
	private final static int SEQ_CODE_FLAG_MASK	= 0x3;
	private final static int SEQ_SHIFT_MASK		= ((1 << SEQ_FLAG_BITS) - 1) ;
	private final static int SEQ_VALUE_EXCEED		= -1;

	private final static String g_seed = "ndrV4JAW3IRivFyjKw0lY6gXM1hcP97Qu=OpkxGsE25CUSBzqHoLbZ~aDtm8TeNf";
	
	/**
	 * 设置初始序列号
	 * 
	 * @author: 刘葳 <snail.liuwei@gmail.com>
	 * @param: 
	 * 		seq - 输入参数，当前数据库中sequence的下一个值
	 * @return: 
	 * 		无
	 */
	public void setSequence(int seq) {
		g_seq_code = seq;
		g_init = true;
	}
	
	/**
	 * 生成短地址
	 * @return
	 */
	public String seqShortLenUrl(){
		
		char shift;
        int seq;
        Random r=new Random(); 
        
        if (!g_init) {
        	return "";
        }
        
        shift = (char) (r.nextInt() & SEQ_SHIFT_MASK);
        seq = seqShift(shift);

        seqIncrease();
        String shortLen = seqGenCode(seq, shift);
		return shortLen ;
	}
	
	/**
	 * 解析短地址
	 * 
	 *  @author: 刘葳 <snail.liuwei@gmail.com>
	 * @param: 
	 * 		code - 输入参数，短地址
	 * @return: 
	 * 		短地址对应的sequence
	 */
	public int seqDecodeCode(String str)
	{
		char[] code = str.toCharArray() ;
        int i;
        int idx = -1;
        int seq = 0;
        char shift = 0;
        int data_bits = SEQ_VALUE_BITS - SEQ_FLAG_BITS;

        for (i=SEQ_CODE_LEN-1; i>-1; i--) {
        		idx = g_seed.indexOf(code[i]);

                if (idx == -1) {
                        return SEQ_VALUE_EXCEED;
                }

                if (i == SEQ_CODE_FLAG_POS) {
                        seq = (seq << data_bits)
                        | idx & SEQ_CODE_FLAG_MASK;
                        shift = (char) (((char)idx) >>> data_bits);
                        continue;
                }

                seq = seq << SEQ_VALUE_BITS | idx;
        }

        return seqShiftReverse(seq, shift);
	}
	
	private int seqShiftReverse(int seq, char bits){
		
        char sf = (char) (bits & SEQ_SHIFT_MASK);

        if (SEQ_SHIFT_LEFT(sf)) {
                return (seq >>> sf)
                        | (seq << (SEQ_CODE_BITS - sf));
        }

        /* shift right */
        return (seq << sf) | (seq >>> (SEQ_CODE_BITS - sf));
	}
	
	
	private int seqShift(char bits){
		
		char sf = (char) (bits & SEQ_SHIFT_MASK);

        if (SEQ_SHIFT_LEFT(sf)) {
                return (g_seq_code << sf) | (g_seq_code >>> (SEQ_CODE_BITS - sf));
        }

        /* shift right */
        return (g_seq_code >>> sf) | (g_seq_code << (SEQ_CODE_BITS - sf));
	}
	
	private boolean SEQ_SHIFT_LEFT(char sf) {
		if ((sf & (1 << (SEQ_FLAG_BITS-1))) == 0) {
			return false;
		}
		
		return true;
	}
	
	private void seqIncrease(){
	    g_seq_code++;
	}
	
	/**
	 * 生成短地址
	 * @param seq
	 * @param shift
	 * @return
	 */
	private String seqGenCode(int seq, char shift){
		char[] code = new char[6] ;
        int idx;
        int i;

        for (i=0; i<SEQ_CODE_LEN; i++) {
            if (i == SEQ_CODE_FLAG_POS) {
                idx = (shift << (SEQ_VALUE_BITS - SEQ_FLAG_BITS))
                                | (seq & SEQ_CODE_FLAG_MASK);

                code[i] = g_seed.charAt(idx);
                seq = seq >>> (SEQ_VALUE_BITS - SEQ_FLAG_BITS);
                continue;
            }

            code[i] = g_seed.charAt(seq & SEQ_CODE_MASK);
            seq = seq >>> SEQ_VALUE_BITS;
        }
        return new String(code) ;
	}
	
	public static void main(String[] args){
		ShortLenUrlUtil t = new ShortLenUrlUtil() ;
		t.setSequence(5) ;
		System.out.println("短地址：" + t.seqShortLenUrl()) ;
		System.out.println("短地址序号：" + t.seqDecodeCode(t.seqShortLenUrl()) );
		System.out.println("短地址序号：" + t.seqDecodeCode("nnnnMv11111")) ;
		System.out.println("短地址序号：" + t.seqDecodeCode("nKdnEn")) ;
	}
	
}


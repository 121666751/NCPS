/*
 * create by hzh in 2014.12.08
 */

package com.union.algorithm;

public class SM3 {

	 long total[] = {0,0};     /*!< number of bytes processed  */
	 long state[] = {0,0,0,0,0,0,0,0};     /*!< intermediate digest state  */
	 byte buffer[ ] = new byte[64];   /*!< data block being processed */

	 byte ipad[] = new byte[64];     /*!< HMAC: inner padding        */
	 byte opad[] = new byte[64];     /*!< HMAC: outer padding        */

	static final byte [] sm3_padding =
	{
	 (byte)0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
	    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	
	//static String sm2_par_dig = "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E9332C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0";
	
	static final byte[] sm2_par_dig = {
		(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFE,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,
		(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,0x00,0x00,0x00,0x00,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFC,
		0x28,(byte) 0xE9,(byte) 0xFA,(byte) 0x9E,(byte) 0x9D,(byte) 0x9F,0x5E,0x34,0x4D,0x5A,(byte) 0x9E,0x4B,(byte) 0xCF,0x65,0x09,(byte) 0xA7,
		(byte) 0xF3,(byte) 0x97,(byte) 0x89,(byte) 0xF5,0x15,(byte) 0xAB,(byte) 0x8F,(byte) 0x92,(byte) 0xDD,(byte) 0xBC,(byte) 0xBD,0x41,0x4D,(byte) 0x94,0x0E,(byte) 0x93,
		0x32,(byte) 0xC4,(byte) 0xAE,0x2C,0x1F,0x19,(byte) 0x81,0x19,0x5F,(byte) 0x99,0x04,0x46,0x6A,0x39,(byte) 0xC9,(byte) 0x94,
		(byte) 0x8F,(byte) 0xE3,0x0B,(byte) 0xBF,(byte) 0xF2,0x66,0x0B,(byte) 0xE1,0x71,0x5A,0x45,(byte) 0x89,0x33,0x4C,0x74,(byte) 0xC7,
		(byte) 0xBC,0x37,0x36,(byte) 0xA2,(byte) 0xF4,(byte) 0xF6,0x77,(byte) 0x9C,0x59,(byte) 0xBD,(byte) 0xCE,(byte) 0xE3,0x6B,0x69,0x21,0x53,
		(byte) 0xD0,(byte) 0xA9,(byte) 0x87,0x7C,(byte) 0xC6,0x2A,0x47,0x40,0x02,(byte) 0xDF,0x32,(byte) 0xE5,0x21,0x39,(byte) 0xF0,(byte) 0xA0,
	};
	
	
	public long getUnsignedInt (long data){     //��int����ת��Ϊ0~4294967295 (0xFFFFFFFF��DWORD)��
        return data&0x0FFFFFFFFL;
     }

	public long getUnsignedV(long d)
	{
		d = getUnsignedInt(d);
		return (0x0FFFFFFFFL - d);
	}
	
	long FF0(long x,long y,long z)
	{
		return getUnsignedInt((x) ^ (y) ^ (z));
	}
	
	long FF1(long x,long y,long z)
	{
		return getUnsignedInt(((x) & (y)) | ((x) & (z)) | ((y) & (z))) ;
	}
	
	long GG0(long x,long y,long z)
	{
		return getUnsignedInt((x) ^ (y) ^ (z));
	}
	
	long GG1(long x,long y,long z)
	{
		return getUnsignedInt(((x) & (y)) | (  (getUnsignedV(x)) & (z)) );
	}
	
	long SHL(long x,int n)
	{
	   return getUnsignedInt(getUnsignedInt(x) << n%32);
	}
	
	long ROTL(long x,int n)
	{
		return getUnsignedInt( SHL(x,n) | (getUnsignedInt(x) >> (32 - n%32)));
	}
	
	long P0(long x)
	{
		return getUnsignedInt(getUnsignedInt(x) ^  ROTL(x,9) ^ ROTL(x,17));
	}
	
	long P1(long x)
	{
		return getUnsignedInt(getUnsignedInt(x) ^  ROTL(x,15) ^ ROTL(x,23));
	}
	
	 
	
	public void Init(){
		total[0] = 0L;
	    total[1] = 0L;

	    state[0] = 0x7380166FL;
	    state[1] = 0x4914B2B9L;
	    state[2] = 0x172442D7L;
	    state[3] = 0x0DA8A0600L;
	    state[4] = 0x0A96F30BCL;
	    state[5] = 0x163138AAL;
	    state[6] = 0x0E38DEE4DL;
	    state[7] = 0x0B0FB0E4EL;
	}
	
	 
	long GET_ULONG_BE(byte b[],int offset,int i)                             
	{    
		long n = 0;
	    n = (   (0xff000000 & (b[offset + i   ] << 24 ))         
	        |   ( 0xff0000 & (b[offset + i + 1] << 16 ))         
	        |   ( 0xff00 & (b[offset + i + 2] <<  8 ))         
	        |   ( 0xff & b[offset + i + 3] )     );   
	    return getUnsignedInt(n);
	}
	 

	 
	void PUT_ULONG_BE(long n,byte b[],int i)                             
	{           
		n = n&(0xffffffffL);
	    b[i    ] =   (byte)(0xff & ( (0xff000000L & n) >> 24 ));        
	    b[i + 1] =   (byte)(0xff & ( (0x00ff0000 & n) >> 16 ));       
	    b[i + 2] =   (byte)(0xff & ( (0x0000ff00 & n) >>  8 ));        
	    b[i + 3] =   (byte)(0xff & n      );    
	}
	 
	void sm3_process( byte data[ ],int offset )
	{
		long SS1, SS2, TT1, TT2, W[] = new long[68],W1[] = new long[64];
		long A, B, C, D, E, F, G, H;
		long T[] = new long[64];
		long Temp1,Temp2,Temp3,Temp4,Temp5;
		int j;
	 
		
		for(j = 0; j < 16; j++)
			T[j] = 0x79CC4519L;
		for(j =16; j < 64; j++)
			T[j] = 0x7A879D8AL;
 
		W[ 0] = GET_ULONG_BE( data, offset, 0 );
		W[ 1] = GET_ULONG_BE( data, offset, 4 );
		W[ 2] = GET_ULONG_BE( data, offset, 8 );
		W[ 3] = GET_ULONG_BE( data, offset, 12 );
		W[ 4] = GET_ULONG_BE( data, offset, 16 );
		W[ 5] = GET_ULONG_BE( data, offset, 20 );
		W[ 6] = GET_ULONG_BE( data, offset, 24 );
		W[ 7] = GET_ULONG_BE( data, offset, 28 );
		W[ 8] = GET_ULONG_BE( data, offset, 32 );
		W[ 9] = GET_ULONG_BE( data, offset, 36 );
		W[10] = GET_ULONG_BE( data, offset, 40 );
		W[11] = GET_ULONG_BE( data, offset, 44 );
		W[12] = GET_ULONG_BE( data, offset, 48 );
		W[13] = GET_ULONG_BE( data, offset, 52 );
		W[14] = GET_ULONG_BE( data, offset, 56 );
		W[15] = GET_ULONG_BE( data, offset, 60 );
        int i = 0;
		
		 
		for(j = 16; j < 68; j++ )
		{
			//W[j] = P1( W[j-16] ^ W[j-9] ^ ROTL(W[j-3],15)) ^ ROTL(W[j - 13],7 ) ^ W[j-6];
			//Why thd release's result is different with the debug's ?
			//Below is okay. Interesting, Perhaps VC6 has a bug of Optimizaiton.
			
			Temp1 = (W[j-16] ^ W[j-9]);
			Temp2 = ROTL(W[j-3],15);
			Temp3 = (Temp1 ^ Temp2);
			Temp4 = P1(Temp3);
			Temp5 =  (ROTL(W[j - 13],7 ) ^ W[j-6]);
			W[j] = (Temp4 ^ Temp5);
		}
		
		for(j =  0; j < 64; j++)
		{
	        W1[j] = (W[j] ^ W[j+4]);
		}
		
	    A = state[0]&0xffffffffL;
	    B = state[1]&0xffffffffL;
	    C = state[2]&0xffffffffL;
	    D = state[3]&0xffffffffL;
	    E = state[4]&0xffffffffL;
	    F = state[5]&0xffffffffL;
	    G = state[6]&0xffffffffL;
	    H = state[7]&0xffffffffL;
	    

	
		for(j =0; j < 16; j++)
		{
			SS1 = (ROTL((ROTL(A,12) + E + ROTL(T[j],j)), 7)); 
			SS2 = (SS1 ^ ROTL(A,12));
			TT1 = (FF0(A,B,C) + D + SS2 + W1[j]);
			TT2 = (GG0(E,F,G) + H + SS1 + W[j]);
			D = getUnsignedInt(C);
			C = ROTL(B,9);
			B = getUnsignedInt(A);
			A = getUnsignedInt(TT1);
			H = getUnsignedInt(G);
			G = ROTL(F,19);
			F = getUnsignedInt(E);
			E = P0(TT2);
		}
	    
		for(j =16; j < 64; j++)
		{
			SS1 = getUnsignedInt(ROTL((ROTL(A,12) + E + ROTL(T[j],j)), 7)); 
			SS2 = getUnsignedInt(SS1 ^ ROTL(A,12));
			TT1 = getUnsignedInt(FF1(A,B,C) + D + SS2 + W1[j]);
			TT2 = getUnsignedInt(GG1(E,F,G) + H + SS1 + W[j]);
			D = getUnsignedInt(C);
			C = ROTL(B,9);
			B = getUnsignedInt(A);
			A = getUnsignedInt(TT1);
			H = getUnsignedInt(G);
			G = ROTL(F,19);
			F = getUnsignedInt(E);
			E = P0(TT2);
		}

	    state[0] = (A ^ state[0])&0x0ffffffffL;
	    state[1] = (B ^ state[1])&0x0ffffffffL;
	    state[2] = (C ^ state[2])&0x0ffffffffL;
	    state[3] = (D ^ state[3])&0x0ffffffffL;
	    state[4] = (E ^ state[4])&0x0ffffffffL;
	    state[5] = (F ^ state[5])&0x0ffffffffL;
	    state[6] = (G ^ state[6])&0x0ffffffffL;
	    state[7] = (H ^ state[7])&0x0ffffffffL;
	}
	
	void memcpy(byte des[],int offset1,byte src[],int offset2,int len)
	{
		int i = 0;
		for(i=0;i<len;i++)
		{
			des[offset1 + i] = src[offset2 + i];
		}
	}
	
	public void Update(byte [] input, int ilen)
	{
		int fill;
		long left;

	    if( ilen <= 0 )
	        return;

	    left = total[0] & 0x3F;
	    fill = 64 - (int)left;

	    total[0] += ilen;
	    total[0] &= 0x0FFFFFFFFL;

	    if( total[0] <  ilen )
	         total[1]++;
        int offset2 = 0;
	    if( left != 0 && ilen >= fill )
	    {
	        memcpy( buffer , (int)left,
	                 input, offset2, fill );
	       
	        sm3_process( buffer, 0 );
	        offset2 += fill;
	        ilen  -= fill;
	        left = 0;
	    }

	     
	    while( ilen >= 64 )
	    {
	        sm3_process(  input, offset2);
	        offset2 += 64;
	        ilen  -= 64;
	    }
        
	    if( ilen > 0 )
	    {
	        memcpy(  buffer , (int)left,
	                input, offset2, ilen );
	    }
	}
	
	public void Update(byte [] input)
	{
        int ilen;
        
        ilen = input.length;
	    if( ilen <= 0 )
	        return;

	    Update(input,ilen);
	}
	
	byte [] Final()
	{
		byte [] digest = new byte[32];
		
		long last, padn;
		long high, low;
	    byte msglen[] = new byte[8];

	    high = getUnsignedInt(( total[0] >> 29 ) | ( total[1] <<  3 ));
	    low  = getUnsignedInt( total[0] <<  3 );

	     
	    PUT_ULONG_BE( high, msglen, 0 );
	    PUT_ULONG_BE( low,  msglen, 4 );

	    last = getUnsignedInt(total[0] & 0x3F);
	    padn = ( last < 56 ) ? ( 56 - last ) : ( 120 - last );
	    Update(  sm3_padding, (int)padn );
	     
	    Update(  msglen, 8 );

	    PUT_ULONG_BE( state[0], digest,  0 );
	    PUT_ULONG_BE( state[1], digest,  4 );
	    PUT_ULONG_BE( state[2], digest,  8 );
	    PUT_ULONG_BE( state[3], digest, 12 );
	    PUT_ULONG_BE( state[4], digest, 16 );
	    PUT_ULONG_BE( state[5], digest, 20 );
	    PUT_ULONG_BE( state[6], digest, 24 );
	    PUT_ULONG_BE( state[7], digest, 28 );
	    
		return digest;
	}
	
	public byte [] Digest( byte [] input, int ilen)
	{
		byte [] output = null;
	    
	    Init();
	    Update(  input, ilen );
	    output = Final( );
        return output;
	}
	
	public byte [] Digest( byte [] input)
	{
		int ilen = input.length;
	    return Digest(input,ilen);
	}
	
	// add by zhouxw 20151209
	public byte [] DigestForSM2SignWithPK(byte[] input, byte[] userid, byte[] PK)
	{
		byte[]	sm3Data;
		byte[]	tmpBuf = new byte[2+userid.length+128+64];
		byte[]	oriHashData = new byte[input.length+32];
		//byte[]	sm2pardig = new byte[128];
		int		userid_bitlen;
		//sm2pardig = hex2byte(sm2_par_dig);
		
		userid_bitlen = userid.length << 3;
		tmpBuf[0] = (byte) ((userid_bitlen >> 8) & 0xFF);
		tmpBuf[1] = (byte) (userid_bitlen & 0xFF);
			
		System.arraycopy(userid, 0, tmpBuf, 2, userid.length);
		System.arraycopy(sm2_par_dig, 0, tmpBuf, 2+userid.length, sm2_par_dig.length);
		System.arraycopy(PK, 0, tmpBuf, 2+userid.length+sm2_par_dig.length, PK.length);
		
		sm3Data = Digest(tmpBuf);

		System.arraycopy(sm3Data, 0, oriHashData, 0, 32);
		System.arraycopy(input, 0, oriHashData, 32, input.length);
		
		sm3Data = Digest(oriHashData);
		
		return sm3Data;
	}
	// add end
	
	public static String byte2hex(byte[] b) { // ������ת�ַ���
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
			if (n < b.length - 1) {
				hs = hs + "";
			}
		}
		return hs.toUpperCase();
	}
	

	public static byte[] hex2byte(String str) { // �ַ���ת������
		int len = str.length();
		String stmp = null;
		byte bt[] = new byte[len / 2];
		for (int n = 0; n < len / 2; n++) {
			stmp = str.substring(n * 2, n * 2 + 2);
			bt[n] = (byte) (java.lang.Integer.parseInt(stmp, 16));
		}
		return bt;
	}

	/*
	public static void main(String []args)
	{
		SM3 sm3 = new SM3();
		String str = "30818902818100E72451C4ED22BE73935BD98749C290B21D44526A3D3FF5A3DF2A4EACBE5E5DB211B6BD5CBC258C193A4D7630538BBB14CA768B5729A4A6F7AF78E9523B4A0D8E28BA18CB02AE29760ABAE7C77BFED76CE0CE431F427B9C275F541B176174E3A4760BC17AFCF4B58BAB12E9259D1DBBDDC81C9BF5E9CCDBFF5E773F7A89BD49550203010001";
		System.out.println("str hex="+str) ;
		str = byte2hex(sm3.Digest(hex2byte(str)));
		System.out.println("sm3 hex=["+str+"]");
		 
		 
	}
	*/
	/*
	public static void main(String []args)
	{
		SM3 sm3 = new SM3();
		String str = "";
		//System.out.println("str hex="+str) ;
		str = byte2hex(sm3.Digest(hex2byte(str)));
		System.out.println("sm3 hex=["+str+"]");
		 
		 
	}
	*/
}

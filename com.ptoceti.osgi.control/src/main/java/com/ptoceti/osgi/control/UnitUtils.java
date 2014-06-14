package com.ptoceti.osgi.control;

import org.osgi.util.measurement.Unit;

public class UnitUtils {

	public static final String UNIT_A = "A";
	public static final String UNIT_C = "C";
	public static final String UNIT_cd = "cd";
	public static final String UNIT_F = "F";
	public static final String UNIT_Gy = "Gy";
	public static final String UNIT_Hz = "Hz";
	public static final String UNIT_J = "J";
	public static final String UNIT_K = "K";
	public static final String UNIT_kat = "kat";
	public static final String UNIT_kg = "kg";
	public static final String UNIT_lx = "lx";
	public static final String UNIT_m = "m";
	public static final String UNIT_m_s = "m_s";
	public static final String UNIT_m_s2 = "m_s2";
	public static final String UNIT_m2 = "m2";
	public static final String UNIT_m3 = "m3";
	public static final String UNIT_mol = "mol";
	public static final String UNIT_N = "N";
	public static final String UNIT_Ohm = "Ohm";
	public static final String UNIT_Pa = "Pa";
	public static final String UNIT_rad = "rad";
	public static final String UNIT_s = "s";
	public static final String UNIT_S = "S";
	public static final String UNIT_T = "T";
	public static final String UNIT_unity = "";
	public static final String UNIT_V = "V";
	public static final String UNIT_W = "W";
	public static final String UNIT_Wb = "Wb";
	
	private static final int UNIT_A_Index = 0;
	private static final int UNIT_C_Index = 1;
	private static final int UNIT_cd_Index = 2;
	private static final int UNIT_F_Index = 3;
	private static final int UNIT_Gy_Index = 4;
	private static final int UNIT_Hz_Index = 5;
	private static final int UNIT_J_Index = 6;
	private static final int UNIT_K_Index = 7;
	private static final int UNIT_kat_Index = 8;
	private static final int UNIT_kg_Index = 9;
	private static final int UNIT_lx_Index = 10;
	private static final int UNIT_m_Index = 11;
	private static final int UNIT_m_s_Index = 12;
	private static final int UNIT_m_s2_Index = 13;
	private static final int UNIT_m2_Index = 14;
	private static final int UNIT_m3_Index = 15;
	private static final int UNIT_mol_Index = 16;
	private static final int UNIT_N_Index = 17;
	private static final int UNIT_Ohm_Index = 18;
	private static final int UNIT_Pa_Index = 19;
	private static final int UNIT_rad_Index = 20;
	private static final int UNIT_s_Index = 21;
	private static final int UNIT_S_Index = 22;
	private static final int UNIT_T_Index = 23;
	private static final int UNIT_unity_Index = 24;
	private static final int UNIT_V_Index = 25;
	private static final int UNIT_W_Index = 26;
	private static final int UNIT_Wb_Index = 27;


	private static String units[] = { UNIT_A, UNIT_C, UNIT_cd, UNIT_F, UNIT_Gy, UNIT_Hz, UNIT_J, UNIT_K, UNIT_kat, UNIT_kg, UNIT_lx, UNIT_m, UNIT_m_s,
		UNIT_m_s2, UNIT_m2, UNIT_m3, UNIT_mol, UNIT_N, UNIT_Ohm, UNIT_Pa, UNIT_rad, UNIT_s, UNIT_S, UNIT_T, UNIT_unity, UNIT_V, UNIT_W, UNIT_Wb };
		
	public static Unit getUnit(String unitName) {
		
		Unit result = null;
		boolean hasFound = false;
		int index;
		
		for( index = 0; index < units.length; index++ ) {
			if( unitName.equals((String) units[index])) {
				hasFound = true;
				break;
			}
		}
		
		if( hasFound == true ) {
			switch( index ) {
				case UNIT_A_Index :
					result = Unit.A;
					break;
				case UNIT_C_Index :
					result = Unit.C;
					break;
				case UNIT_cd_Index:
					result = Unit.cd;
					break;
				case UNIT_F_Index:
					result = Unit.F;
					break;
				case UNIT_Gy_Index:
					result = Unit.Gy;
					break;
				case UNIT_Hz_Index:
					result = Unit.Hz;
					break;
				case UNIT_J_Index:
					result = Unit.J;
					break;
				case UNIT_K_Index:
					result = Unit.K;
					break;
				case UNIT_kat_Index:
					result = Unit.kat;
					break;
				case UNIT_kg_Index:
					result = Unit.kg;
					break;
				case UNIT_lx_Index:
					result = Unit.lx;
					break;
				case UNIT_m_Index:
					result = Unit.m;
					break;
				case UNIT_m_s_Index:
					result = Unit.m_s;
					break;
				case UNIT_m_s2_Index:
					result = Unit.m_s2;
					break;
				case UNIT_m2_Index:
					result = Unit.m2;
					break;
				case UNIT_m3_Index:
					result = Unit.m3;
					break;
				case UNIT_mol_Index:
					result = Unit.mol;
					break;
				case UNIT_N_Index:
					result = Unit.N;
					break;
				case UNIT_Ohm_Index:
					result = Unit.Ohm;
					break;
				case UNIT_Pa_Index:
					result = Unit.Pa;
					break;
				case UNIT_rad_Index:
					result = Unit.rad;
					break;
				case UNIT_s_Index:
					result = Unit.s;
					break;
				case UNIT_S_Index:
					result = Unit.S;
					break;
				case UNIT_T_Index:
					result = Unit.T;
					break;
				case UNIT_unity_Index:
					result = Unit.unity;
					break;
				case UNIT_V_Index:
					result = Unit.V;
					break;
				case UNIT_W_Index:
					result = Unit.W;
					break;
				case UNIT_Wb_Index:
					result = Unit.cd;
					break;
				default :
					result = null;
					break;
			}
		}
		
		return result;
	}
}

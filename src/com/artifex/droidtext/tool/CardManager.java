/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.artifex.droidtext.tool;



import java.io.IOException;


import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;
import android.util.Log;

@SuppressLint("NewApi")
public final class CardManager {
	private static final String SP = "<br/><img src=\"spliter\"/><br/>";

	public static String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;

	static {
		try {
			TECHLISTS = new String[][] { { IsoDep.class.getName() },
					{ NfcV.class.getName() }, { NfcF.class.getName() }, };

			FILTERS = new IntentFilter[] { new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (Exception e) {
		}
	}

	public static String buildResult(String n, String i, String d, String x) {
		if (n == null)
			return null;

		final StringBuilder s = new StringBuilder();

		s.append("<br/><b>").append(n).append("</b>");

		if (i != null)
			s.append(SP).append(i);

		if (d != null)
			s.append(SP).append(d);

		if (x != null)
			s.append(SP).append(x);

		return s.append("<br/><br/>").toString();
	}

	public static String load(Parcelable parcelable, Resources res) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			try {
				isodep.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("isodep", Util.toHexString(isodep.getTag().getId(), 0, isodep.getTag().getId().length));
			return Util.toHexString(isodep.getTag().getId(), 0, isodep.getTag().getId().length);
		}
		final NfcV nfcv = NfcV.get(tag);
		if (nfcv != null) {
			try {
				nfcv.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("nfcv", Util.toHexString(nfcv.getTag().getId(), 0, nfcv.getTag().getId().length));
			return Util.toHexString(nfcv.getTag().getId(), 0, nfcv.getTag().getId().length);
		}

		final NfcF nfcf = NfcF.get(tag);
		if (nfcf != null) {
			try {
				nfcf.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("nfcf", Util.toHexString(nfcf.getTag().getId(), 0, nfcf.getTag().getId().length));
			return Util.toHexString(nfcf.getTag().getId(), 0, nfcf.getTag().getId().length);
		}
		final NfcA nfca = NfcA.get(tag);
		if (nfca != null) {
			try {
				nfca.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("nfca", Util.toHexString(nfca.getTag().getId(), 0, nfca.getTag().getId().length));
			return Util.toHexString(nfca.getTag().getId(), 0, nfca.getTag().getId().length);
		}
		final NfcB nfcb = NfcB.get(tag);
		if (nfcb != null) {
			try {
				nfcb.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("nfcb", Util.toHexString(nfcb.getTag().getId(), 0, nfcb.getTag().getId().length));
			return Util.toHexString(nfcb.getTag().getId(), 0, nfcb.getTag().getId().length);
		}
		final Ndef ndef = Ndef.get(tag);
		if (ndef != null) {
			try {
				ndef.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("ndef", Util.toHexString(ndef.getTag().getId(), 0, ndef.getTag().getId().length));
			return Util.toHexString(ndef.getTag().getId(), 0, ndef.getTag().getId().length);
		}
		final NdefFormatable NdefFor = NdefFormatable.get(tag);
		if (NdefFor != null) {
			try {
				NdefFor.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("NdefFor", Util.toHexString(NdefFor.getTag().getId(), 0, NdefFor.getTag().getId().length));
			return Util.toHexString(NdefFor.getTag().getId(), 0, NdefFor.getTag().getId().length);
		}
		final MifareClassic Mif = MifareClassic.get(tag);
		if (Mif != null) {
			try {
				Mif.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Mif", Util.toHexString(Mif.getTag().getId(), 0, Mif.getTag().getId().length));
			return Util.toHexString(Mif.getTag().getId(), 0, Mif.getTag().getId().length);
		}
		final MifareUltralight Mifare = MifareUltralight.get(tag);
		if (Mifare != null) {
			try {
				Mifare.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("Mifare", Util.toHexString(Mifare.getTag().getId(), 0, Mifare.getTag().getId().length));
			return Util.toHexString(Mifare.getTag().getId(), 0, Mifare.getTag().getId().length);
		}
		return null;
	}
}

package com.ab.nfc;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

public class NFCReader {
	
	public String read(Intent intent){
		Parcelable [] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage ndefMsg = (NdefMessage) rawArray[0];
		NdefRecord ndefRecord = ndefMsg.getRecords()[0];
		return parse(ndefRecord);
	}

	private String parse(NdefRecord ndefRecord) {
		 /*
         * payload[0] contains the "Status Byte Encodings" field, per the
         * NFC Forum "Text Record Type Definition" section 3.2.1.
         *
         * bit7 is the Text Encoding Field.
         *
         * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
         * The text is encoded in UTF16
         *
         * Bit_6 is reserved for future use and must be set to zero.
         *
         * Bits 5 to 0 are the length of the IANA language code.
         */
		byte[] payload = ndefRecord.getPayload();
		String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0077;
        String text="";
        try {
			text = new String(payload, languageCodeLength + 1,payload.length - languageCodeLength - 1, textEncoding);
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} 
		return text;
	}
}

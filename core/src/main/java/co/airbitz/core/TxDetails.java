/**
 * Copyright (c) 2014, Airbitz Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Redistribution or use of modified source code requires the express written
 *    permission of Airbitz Inc.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the Airbitz Project.
 */

package co.airbitz.core;

import co.airbitz.internal.SWIGTYPE_p_int64_t;
import co.airbitz.internal.core;
import co.airbitz.internal.tABC_TxDetails;
import co.airbitz.internal.Jni;

/**
 * Used internally to map transaction details from core
 */
class TxDetails extends tABC_TxDetails {
    long mAmountSatoshi; /** amount of bitcoins in satoshi (including fees if any) */
    long mAmountFeesAirbitzSatoshi;   /** airbitz fees in satoshi */
    long mAmountFeesMinersSatoshi;  /** miners fees in satoshi */
    double mAmountCurrency;  /** amount in currency */
    String mName;   /** payer or payee */
    long mBizId; /** payee business-directory id (0 otherwise) */
    String mCategory;   /** category for the transaction */
    String mNotes;  /** notes for the transaction */

    public TxDetails(long pv) {
        super(pv, false);
        if (pv != 0) {
            mAmountSatoshi = Jni.get64BitLongAtPtr(
                Jni.getCPtr(getAmountSatoshi()));
            mAmountFeesAirbitzSatoshi = Jni.get64BitLongAtPtr(
                Jni.getCPtr(getAmountFeesAirbitzSatoshi()));
            mAmountFeesMinersSatoshi = Jni.get64BitLongAtPtr(
                Jni.getCPtr(getAmountFeesMinersSatoshi()));
            mAmountCurrency = super.getAmountCurrency();

            mName = super.getSzName();
            mBizId = super.getBizId();
            mCategory = super.getSzCategory();
            mNotes = super.getSzNotes();
        }
    }


    public long getmAmountSatoshi() { return mAmountSatoshi; }

    public long getmAmountFeesAirbitzSatoshi() { return mAmountFeesAirbitzSatoshi; }

    public long getmAmountFeesMinersSatoshi() { return mAmountFeesMinersSatoshi; }

    public double getmAmountCurrency() { return mAmountCurrency; }
}

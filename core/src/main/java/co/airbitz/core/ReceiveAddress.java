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

import co.airbitz.internal.Jni;
import co.airbitz.internal.SWIGTYPE_p_int;
import co.airbitz.internal.SWIGTYPE_p_long;
import co.airbitz.internal.SWIGTYPE_p_p_char;
import co.airbitz.internal.SWIGTYPE_p_p_unsigned_char;
import co.airbitz.internal.SWIGTYPE_p_uint64_t;
import co.airbitz.internal.SWIGTYPE_p_unsigned_int;
import co.airbitz.internal.core;
import co.airbitz.internal.tABC_CC;
import co.airbitz.internal.tABC_Error;
import co.airbitz.internal.tABC_TxDetails;

/**
 * ReceiveAddress is returned by {@link Wallet#newReceiveRequest
 * newReceiveRequest} and {@link Wallet#fetchReceiveRequest
 * fetchReceiveRequest}. This class can be used to generate qrcodes and URIs
 * to share with others to receive payments.
 */
public class ReceiveAddress {
    private static String TAG = ReceiveAddress.class.getSimpleName();

    private Account mAccount;
    private Wallet mWallet;
    private String mAddress;
    private tABC_TxDetails mDetails;
    private MetadataSet mMeta;
    private long mSatoshi;
    private String mUri;
    private byte[] mQrCode;

    private String mUriLabel;
    private String mUriCategory;
    private String mUriRet;
    private String mUriMessage;
    private long mBizId;

    ReceiveAddress(Account account, Wallet wallet) {
        init(account, wallet, null);
    }

    ReceiveAddress(Account account, Wallet wallet, String address) {
        init(account, wallet, address);
    }

    void init(Account account, Wallet wallet, String address) {
        this.mAccount = account;
        this.mWallet = wallet;
        this.mDetails = new tABC_TxDetails();
        this.mMeta = new MetadataSet();
        this.mAddress = address;
        start();
        mMeta.mChangeListener = new MetadataSet.OnChangeListener() {
            public void onChange() {
                update();
            }
        };
        // NOTE: Don't run update until we have an ABC_GetReceiveRequest to
        // load the meta data otherwise, for existing requests, we will write
        // an empty receive request.
        if (null == address) {
            update();
        }
    }


    /**
     * @return the qrcode
     */
    public byte[] qrcode() {
        return mQrCode;
    }

    /**
     * @return the URI
     */
    public String uri() {
        return mUri;
    }

    /**
     * Set the label for this receive request.
     * @return itself with the updated values
     */
    public ReceiveAddress uriLabel(String label) {
        mUriLabel = label;
        update();
        return this;
    }

    /**
     * Set the category for this receive request.
     * @return itself with the updated values
     */
    public ReceiveAddress uriCategory(String category) {
        mUriCategory = category;
        update();
        return this;
    }

    /**
     * Set the message for this receive request.
     * @return itself with the updated values
     */
    public ReceiveAddress uriMessage(String message) {
        mUriMessage = message;
        update();
        return this;
    }

    /**
     * Set the return URI for this receive request.
     * @return itself with the updated values
     */
    public ReceiveAddress uriReturn(String ret) {
        mUriRet = ret;
        update();
        return this;
    }

    /**
     * @return the address for this request
     */
    public String address() {
        return mAddress;
    }

    /**
     * @return the metadata for this request
     */
    public MetadataSet meta() {
        return mMeta;
    }

    /**
     * Set the amount for this receive request.
     * @return itself with the updated values
     */
    public ReceiveAddress amount(long amount) {
        mSatoshi = amount;
        update();
        return this;
    }

    /**
     * @return itself with the updated values
     */
    public long amount() {
        return mSatoshi;
    }

    /**
     * Indicates that this address is a priority and should be checked more
     * frequently with the bitcoin network for any changes in its balance.
     * @param prior true prioritizes and false deprioritizes
     */
    public void prioritize(boolean prior) {
        tABC_Error error = new tABC_Error();
        if (prior) {
            core.ABC_PrioritizeAddress(
                    mAccount.username(), mAccount.password(),
                    mWallet.id(), mAddress, error);
        } else {
            core.ABC_PrioritizeAddress(
                    mAccount.username(), mAccount.password(),
                    mWallet.id(), null, error);
        }
    }

    /**
     * Finalizing a request marks the address as used and it will not be used
     * for future requests. The metadata will also be written for this address.
     * This is useful so that when a future payment comes in, the metadata can
     * be auto-populated.
     * @return true if the request was successfully finalized.
     */
    public boolean finalizeRequest() {
        tABC_Error error = new tABC_Error();
        core.ABC_FinalizeReceiveRequest(
                mAccount.username(), mAccount.password(),
                mWallet.id(), mAddress, error);
        return error.getCode() == tABC_CC.ABC_CC_Ok;
    }

    /**
     * Canceling marks the address as reusable, so that future requests might
     * use it.
     * @return true if the request was successfully cancelled.
     */
    public boolean cancel() {
        tABC_Error error = new tABC_Error();
        core.ABC_CancelReceiveRequest(
                mAccount.username(), mAccount.password(),
                mWallet.id(), mAddress, error);
        return error.getCode() == tABC_CC.ABC_CC_Ok;
    }

    private void start() {
        tABC_Error error = new tABC_Error();
        if (null == mAddress) {
            SWIGTYPE_p_long lp = core.new_longp();
            SWIGTYPE_p_p_char pRequestID = core.longp_to_ppChar(lp);
            core.ABC_CreateReceiveRequest(
                mAccount.username(), mAccount.password(),
                mWallet.id(), pRequestID, error);
            if (tABC_CC.ABC_CC_Ok == error.getCode()) {
                mAddress = Jni.getStringAtPtr(core.longp_value(lp));
            }
        }
    }

    private boolean update() {
        Jni.set64BitLongAtPtr(Jni.getCPtr(mDetails) + 0, mSatoshi);
        tABC_Error error = new tABC_Error();
        mDetails.setAmountFeesAirbitzSatoshi(core.new_int64_tp());
        mDetails.setAmountFeesMinersSatoshi(core.new_int64_tp());
        mDetails.setAmountCurrency(mSatoshi);
        mDetails.setSzName(mMeta.name());
        mDetails.setSzNotes(mMeta.notes());
        mDetails.setSzCategory(mMeta.category());
        if (0 < mMeta.bizid()) {
            mDetails.setBizId(mMeta.bizid());
        }

        core.ABC_ModifyReceiveRequest(
                mAccount.username(), mAccount.password(),
                mWallet.id(), mAddress, mDetails, error);
        if (tABC_CC.ABC_CC_Ok == error.getCode()) {
            setupQrCode();
            return true;
        }
        return false;
    }

    private void setupQrCode() {
        tABC_Error error = new tABC_Error();
        SWIGTYPE_p_long lp = core.new_longp();
        SWIGTYPE_p_p_char ppURI = core.longp_to_ppChar(lp);

        SWIGTYPE_p_uint64_t ua = core.new_uint64_tp();
        Jni.set64BitLongAtPtr(Jni.getCPtr(ua), mSatoshi);

        core.ABC_AddressUriEncode(mAddress, ua,
                mUriLabel, mUriMessage, mUriCategory, mUriRet, ppURI, error);
        mUri = Jni.getStringAtPtr(core.longp_value(lp));
        mQrCode = AirbitzCore.getApi().qrEncode(mUri);
    }
}

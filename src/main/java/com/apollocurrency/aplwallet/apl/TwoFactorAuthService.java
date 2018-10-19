/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl;

public interface TwoFactorAuthService {
    /**
     * Enable 2fa for account specified by accountId
     * @param accountId id of account, which want to enable 2fa
     * @return <ul>Dto, which consist of:
     * <li>QR code url for creating QR code and scan it by QR code reader</li>
     * <li>plain 2fa secret word</li>
     * <li>status2FA - if OK - 2fa was enabled successfully, otherwise status hold error cause</li>
     * </ul>
     */
    TwoFactorAuthDetails enable(long accountId);

    /**
     * Disable 2fa for account specified by accountId, require authCode to perform 2fa firstly.
     * @param accountId id of account, which want to disable 2fa
     * @param authCode temporal code number based on 2fa secret word and generated by user device, app, etc
     * @return OK status if account successfully passed 2fa and 2fa was disabled, otherwise result hold error cause
     */
    Status2FA disable(long accountId, int authCode);

    /**
     * Check is a 2fa enabled for account specified by accountId
     * @param accountId id of account, which 2fa should be verified
     * @return true if account has 2fa or false if account has not 2fa
     */
    boolean isEnabled(long accountId);

    /**
     * Perform 2fa using user account and generated temporal authCode
     * @param accountId id of account which should pass 2fa
     * @param authCode temporal code number based on 2fa secret word and generated by user device, app, etc
     * @return status OK if authCode is appropriate for this account, otherwise - status hold error cause
     */
    Status2FA tryAuth(long accountId, int authCode);

    /**
     * Confirm enabled 2fa for account specified by accountId
     * @param accountId id of account
     * @param authCode temporal code number based on 2fa secret word and generated by user device, app, etc
     * @return status OK if 2fa was confirmed successfully for this account, otherwise status hold error cause
     */
    Status2FA confirm(long accountId, int authCode);

    enum Status2FA {
        OK, NOT_FOUND, ALREADY_CONFIRMED, INCORRECT_CODE, INTERNAL_ERROR, NOT_ENABLED, ALREADY_ENABLED, NOT_CONFIRMED
    }
}

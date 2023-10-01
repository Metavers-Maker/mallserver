//package com.muling.mall.ums.util;
//
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.google.common.collect.Lists;
//import com.muling.common.exception.BizException;
//import com.muling.common.result.ResultCode;
//import lombok.extern.slf4j.Slf4j;
//import org.zetrix.SDK;
//import org.zetrix.common.ToBaseUnit;
//import org.zetrix.model.request.*;
//import org.zetrix.model.request.operation.BaseOperation;
//import org.zetrix.model.request.operation.ContractInvokeByGasOperation;
//import org.zetrix.model.request.operation.GasSendOperation;
//import org.zetrix.model.response.*;
//import org.zetrix.model.response.result.BlockGetInfoResult;
//import org.zetrix.model.response.result.BlockGetTransactionsResult;
//import org.zetrix.model.response.result.ContractCallResult;
//import org.zetrix.model.response.result.data.Signature;
//import org.zetrix.model.response.result.data.TransactionHistory;
//
//import java.math.BigDecimal;
//
//@Slf4j
//public class ChainOperateUtils {
//    static {
//        SDKConfigure sdkConfigure = new SDKConfigure();
//        sdkConfigure.setHttpConnectTimeOut(5000);
//        sdkConfigure.setHttpReadTimeOut(5000);
////        sdkConfigure.setUrl("http://seed1-node.bubi.cn");
////        sdkConfigure.setUrl("http://20.205.238.226:19333");
////        sdkConfigure.setUrl("http://74.120.168.17:19333");
//        sdkConfigure.setUrl("http://52.81.215.222:19333");
////        sdkConfigure.setUrl("https://test-node.zetrix.com");
//
//        SDK.getInstance(sdkConfigure);
//    }
//
//    /**
//     * 创建账户
//     */
//    public static String createAccount() {
//        AccountCreateResponse accountCreateResponse = SDK.getSdk().getAccountService().create();
//        return accountCreateResponse.getResult().getAddress();
//    }
//
//    /**
//     * 获取资方账户的Nonce
//     *
//     * @param accountAddress
//     * @return
//     */
//    public static long getAccountNonce(String accountAddress) throws BizException {
//
//        AccountGetNonceRequest request = new AccountGetNonceRequest();
//        request.setAddress(accountAddress);
//
//        // Call getNonce
//        AccountGetNonceResponse response = SDK.getSdk().getAccountService().getNonce(request);
//        if (0 == response.getErrorCode()) {
//            long nonce = response.getResult().getNonce();
//            log.info("getAccountNonce url:[{}] , input:[{}] ,output:[{}]", SDK.getSdk().getUrl(), accountAddress, JSONUtil.toJsonStr(response));
//            return nonce;
//        } else {
//            log.error("getAccountNonce url:[{}] , input:[{}] ,output:[{}]", SDK.getSdk().getUrl(), accountAddress, JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    /**
//     * mint操作
//     *
//     * @return
//     */
//    public static BaseOperation[] buildMintOperation(String invokeAddress, String contractAddress, String destAddress, String id, String uri) {
//        // build transfer method input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "mint");
//        JSONObject transferParams = new JSONObject();
//
//        transferParams.set("platform", true);
//        transferParams.set("to", destAddress);
//        transferParams.set("id", id);
//        transferParams.set("value", "1");
//        transferParams.set("uri", uri);
//
//        transferInput.set("params", transferParams);
//
//        // build send gas operation to transfer CGO
//        ContractInvokeByGasOperation transferOperation = new ContractInvokeByGasOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setContractAddress(contractAddress);
//        transferOperation.setZtxAmount(0L);
//        transferOperation.setInput(transferInput.toString());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildMintOperation transferInput:[{}] ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferInput), JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * 序列化交易
//     *
//     * @param nonce
//     * @param operations
//     * @return
//     */
//    public static String seralizeTransaction(String senderAddresss, String fee, Long nonce, BaseOperation[] operations) throws BizException {
//
//        // The account address to create contract and issue token
////        String senderAddresss = "adxSXVGt5ujjAe4hr11VqhjWpJdKqn6QfDVUX";
//        // The gasPrice is fixed at 1000L, the unit is UGas
//        Long gasPrice = 1000L;
//        // Set up the maximum cost 10.08Gas
//        Long feeLimit = ToBaseUnit.ToUGas(fee);
//        // Nonce should add 1
//        nonce += 1;
//
//        // Build transaction  Blob
//        TransactionBuildBlobRequest transactionBuildBlobRequest = new TransactionBuildBlobRequest();
//        transactionBuildBlobRequest.setSourceAddress(senderAddresss);
//        transactionBuildBlobRequest.setNonce(nonce);
//        transactionBuildBlobRequest.setFeeLimit(feeLimit);
//        transactionBuildBlobRequest.setGasPrice(gasPrice);
//        for (int i = 0; i < operations.length; i++) {
//            transactionBuildBlobRequest.addOperation(operations[i]);
//        }
//        TransactionBuildBlobResponse response = SDK.getSdk().getTransactionService().buildBlob(transactionBuildBlobRequest);
//        if (response.getErrorCode() == 0) {
//            String transactionBlob = response.getResult().getTransactionBlob();
//            log.info("seralizeTransaction url:[{}] ,input:[{}] ,output:[{}]", SDK.getSdk().getUrl(), JSONUtil.toJsonStr(operations), JSONUtil.toJsonStr(response));
//            return transactionBlob;
//        } else {
//            log.error("seralizeTransaction url:[{}] ,input:[{}] ,output:[{}]", SDK.getSdk().getUrl(), JSONUtil.toJsonStr(operations), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//
//    }
//
//
//    /**
//     * 签名交易
//     *
//     * @param senderPrivateKey
//     * @param transactionBlob
//     * @return
//     */
//    public static Signature[] signTransaction(String senderPrivateKey, String transactionBlob) throws BizException {
//        // The account private key to create contract and issue token
////        String senderPrivateKey = "privbs4iBCugQeb2eiycU8RzqkPqd28eaAYrRJGwtJTG8FVHjwAyjiyC";
//
//        // Sign transaction BLob
//        TransactionSignRequest transactionSignRequest = new TransactionSignRequest();
//        transactionSignRequest.setBlob(transactionBlob);
//        transactionSignRequest.addPrivateKey(senderPrivateKey);
//        TransactionSignResponse response = SDK.getSdk().getTransactionService().sign(transactionSignRequest);
//        if (response.getErrorCode() == 0) {
//            Signature[] signatures = response.getResult().getSignatures();
//            log.info("signTransaction input:[{}] ,output:[{}]", JSONUtil.toJsonStr(transactionSignRequest), JSONUtil.toJsonStr(response));
//            return signatures;
//        } else {
//            log.error("signTransaction input:[{}] ,output:[{}]", JSONUtil.toJsonStr(transactionSignRequest), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    /**
//     * 发送交易
//     *
//     * @param transactionBlob
//     * @param signatures
//     * @return
//     */
//    public static String submitTransaction(String transactionBlob, Signature[] signatures) throws BizException {
//
//        // Submit transaction
//        TransactionSubmitRequest transactionSubmitRequest = new TransactionSubmitRequest();
//        transactionSubmitRequest.setTransactionBlob(transactionBlob);
//        transactionSubmitRequest.setSignatures(signatures);
//        TransactionSubmitResponse response = SDK.getSdk().getTransactionService().submit(transactionSubmitRequest);
//        if (0 == response.getErrorCode()) {
//            String hash = response.getResult().getHash();
//            log.info("submitTransaction input:[{}] ,output:[{}]", JSONUtil.toJsonStr(transactionSubmitRequest), JSONUtil.toJsonStr(response));
//            return hash;
//        } else {
//            log.error("submitTransaction input:[{}] ,output:[{}]", JSONUtil.toJsonStr(transactionSubmitRequest), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    public static String execTransaction(String senderPrivateKey, String senderAddress, BaseOperation[] baseOperations) throws BizException {
//
//        String fee = "2";
//        long nonce = ChainOperateUtils.getAccountNonce(senderAddress);
//        String transactionBlob = ChainOperateUtils.seralizeTransaction(senderAddress, fee, nonce, baseOperations);
//        Signature[] signatures = ChainOperateUtils.signTransaction(senderPrivateKey, transactionBlob);
//        String txHash = ChainOperateUtils.submitTransaction(transactionBlob, signatures);
//        return txHash;
//    }
//
//    /**
//     * 查询交易是否执行成功-1
//     *
//     * @param txHash
//     * @return
//     */
//    public static int checkTransactionStatus(String txHash) throws BizException {
//        // Init request
//        TransactionGetInfoRequest request = new TransactionGetInfoRequest();
//        request.setHash(txHash);
//
//        // Call getInfo
//        TransactionGetInfoResponse response = SDK.getSdk().getTransactionService().getInfo(request);
//        int errorCode = response.getErrorCode();
//        if (errorCode == 0) {
//            TransactionHistory transactionHistory = response.getResult().getTransactions()[0];
//            errorCode = transactionHistory.getErrorCode();
//            log.info("checkTransactionStatus input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonPrettyStr(response));
//        } else {
//            log.error("checkTransactionStatus input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonPrettyStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//        return errorCode;
//    }
//
//    /**
//     * 查询交易
//     *
//     * @param txHash
//     * @return
//     */
//    public static TransactionHistory[] getTransaction(String txHash) throws BizException {
//        // Init request
//        TransactionGetInfoRequest request = new TransactionGetInfoRequest();
//        request.setHash(txHash);
//
//        // Call getInfo
//        TransactionGetInfoResponse response = SDK.getSdk().getTransactionService().getInfo(request);
//        int errorCode = response.getErrorCode();
//        if (errorCode == 0) {
//            TransactionHistory[] transactionHistory = response.getResult().getTransactions();
//            log.info("checkTransactionStatus input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonPrettyStr(response));
//            return transactionHistory;
//        } else {
//            log.error("checkTransactionStatus input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonPrettyStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    public static String dTHex(Long id) {
//
//        return String.format("%064x", id);
//    }
//
//    public static Long hexTD(String hexId) {
//        return Long.parseLong(hexId, 16);
//    }
//
//
//    /**
//     * balanceOf操作
//     *
//     * @return
//     */
//    public static String buildBalanceOfOperation(String ownerAddress, String id) {
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "balanceOf");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("platform", true);
//        transferParams.set("owner", ownerAddress);
//        transferParams.set("id", id);
//
//        transferInput.set("params", transferParams);
//
//
//        log.info("buildBalanceOfOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
//
//        return transferInput.toString();
//    }
//
//    /**
//     * getManager
//     *
//     * @return
//     */
//    public static String buildGetManagerOperation() {
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "getManager");
//
//        log.info("buildBalanceOfOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
//
//        return transferInput.toString();
//    }
//
//    /**
//     * GetOperators
//     *
//     * @return
//     */
//    public static String buildGetOperatorsOperation() {
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "getOperators");
//
//        log.info("buildGetOperatorsOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
//
//        return transferInput.toString();
//    }
//
//
//    /**
//     * getPledge操作
//     *
//     * @return
//     */
//    public static String buildGetPledgeOperation(String id) {
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "getPledge");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("id", id);
//
//        transferInput.set("params", transferParams);
//
//
//        log.info("buildGetPledgeOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
//
//        return transferInput.toString();
//    }
//
//    /**
//     * setURI操作
//     *
//     * @return
//     */
//    public static String buildSetURIOperation(String id, String uri) {
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "setURI");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("uri", uri);
//        transferParams.set("id", id);
//
//        transferInput.set("params", transferParams);
//
//        log.info("buildSetURIOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
//
//        return transferInput.toString();
//    }
//
////    /**
////     * free操作
////     *
////     * @return
////     */
////    public static String buildFreeOperation(String hexId) {
////        // Init input
////        JSONObject transferInput = new JSONObject();
////        transferInput.set("method", "free");
////
////        JSONObject transferParams = new JSONObject();
////        transferParams.set("id", hexId);
////
////        transferInput.set("params", transferParams);
////
////        log.info("buildSetURIOperation input:[{}]", JSONUtil.toJsonStr(transferInput));
////
////        return transferInput.toString();
////    }
//
//    /**
//     * free操作
//     *
//     * @return
//     */
//    public static BaseOperation[] buildFreeOperation(String invokeAddress, String contractAddress, String hexId) {
//        // build transfer method input
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "free");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("id", hexId);
//
//        transferInput.set("params", transferParams);
//
//        // build send gas operation to transfer CGO
//        ContractInvokeByGasOperation transferOperation = new ContractInvokeByGasOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setContractAddress(contractAddress);
//        transferOperation.setZtxAmount(0L);
//        transferOperation.setInput(transferInput.toString());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildMintOperation transferInput:[{}] ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferInput), JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * lock操作
//     *
//     * @return
//     */
//    public static BaseOperation[] buildLockOperation(String invokeAddress, String contractAddress, String hexId) {
//        // build transfer method input
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "lock");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("id", hexId);
//
//        transferInput.set("params", transferParams);
//
//        // build send gas operation to transfer CGO
//        ContractInvokeByGasOperation transferOperation = new ContractInvokeByGasOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setContractAddress(contractAddress);
//        transferOperation.setZtxAmount(0L);
//        transferOperation.setInput(transferInput.toString());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildMintOperation transferInput:[{}] ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferInput), JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * 提币
//     *
//     * @return
//     */
//    public static BaseOperation[] buildWithdrawOperation(String invokeAddress, String contractAddress, String hexId) {
//        // build transfer method input
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "withdraw");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("id", hexId);
//
//        transferInput.set("params", transferParams);
//
//        // build send gas operation to transfer CGO
//        ContractInvokeByGasOperation transferOperation = new ContractInvokeByGasOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setContractAddress(contractAddress);
//        transferOperation.setZtxAmount(0L);
//        transferOperation.setInput(transferInput.toString());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildMintOperation transferInput:[{}] ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferInput), JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * 提币
//     *
//     * @return
//     */
//    public static BaseOperation[] buildGasSendOperation(String invokeAddress, String targetAddress, BigDecimal amount) {
//        GasSendOperation transferOperation = new GasSendOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setDestAddress(targetAddress);
//        transferOperation.setAmount(amount.longValue());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildGasSendOperation ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * 设置拥有者
//     *
//     * @return
//     */
//    public static BaseOperation[] buildSetOwnerOperation(String invokeAddress, String contractAddress, String destAddress, String hexId) {
//        // build transfer method input
//        // Init input
//        JSONObject transferInput = new JSONObject();
//        transferInput.set("method", "setOwner");
//
//        JSONObject transferParams = new JSONObject();
//        transferParams.set("id", hexId);
//        transferParams.set("owner", destAddress);
//
//        transferInput.set("params", transferParams);
//
//        // build send gas operation to transfer CGO
//        ContractInvokeByGasOperation transferOperation = new ContractInvokeByGasOperation();
//        transferOperation.setSourceAddress(invokeAddress);
//        transferOperation.setContractAddress(contractAddress);
//        transferOperation.setZtxAmount(0L);
//        transferOperation.setInput(transferInput.toString());
//
//        BaseOperation[] operations = {transferOperation};
//
//        log.info("buildMintOperation transferInput:[{}] ,transferOperation:[{}] ", JSONUtil.toJsonStr(transferInput), JSONUtil.toJsonStr(transferOperation));
//
//        return operations;
//    }
//
//    /**
//     * @param contractAddress
//     * @param sourceAddress
//     * @param input
//     * @return
//     * @throws BizException
//     */
//    public static ContractCallResult queryContract(String contractAddress, String sourceAddress, String input) throws BizException {
//        // Init request
//        ContractCallRequest request = new ContractCallRequest();
//        request.setContractAddress(contractAddress);
//        request.setFeeLimit(10000000000L);
//        request.setOptType(2);
//        request.setInput(input);
//        request.setSourceAddress(sourceAddress);
//
//        // Call call
//        ContractCallResponse response = SDK.getSdk().getContractService().call(request);
//        if (response.getErrorCode() == 0) {
//            ContractCallResult result = response.getResult();
//            log.info("queryContract input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            return result;
//        } else {
//            log.error("queryContract input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//
//    }
//
//
//    /**
//     * 获得最新区块高度
//     *
//     * @return
//     * @throws BizException
//     */
//    public static Long getLastNumber() throws BizException {
//        // 调用getNumber接口
//        BlockGetNumberResponse response = SDK.getSdk().getBlockService().getNumber();
//        if (0 == response.getErrorCode()) {
//            Long blockNumber = response.getResult().getHeader().getBlockNumber();
//            log.info("getLastNumber  url:[{}] ,output:[{}]", SDK.getSdk().getUrl(), JSONUtil.toJsonStr(response));
//            return blockNumber;
//        } else {
//            log.error("getLastNumber url:[{}] ,output:[{}]", SDK.getSdk().getUrl(), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//
//    }
//
//    /**
//     * getTransactions
//     *
//     * @param blockNumber
//     * @return
//     */
//    public static BlockGetTransactionsResult getTransactions(Long blockNumber) throws BizException {
//
//        // 初始化请求参数
//        BlockGetTransactionsRequest request = new BlockGetTransactionsRequest();
//        request.setBlockNumber(blockNumber);
//
//        // 调用getTransactions接口
//        BlockGetTransactionsResponse response = SDK.getSdk().getBlockService().getTransactions(request);
//        if (0 == response.getErrorCode() || 4 == response.getErrorCode()) {
//            BlockGetTransactionsResult result = response.getResult();
//            log.info("getTransactions url:[{}]  input:[{}] ,output:[{}]",SDK.getSdk().getUrl(), JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            return result;
//        } else {
//            log.error("getTransactions url:[{}]  input:[{}] ,output:[{}]",SDK.getSdk().getUrl(), JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    /**
//     * getBlockInfo
//     *
//     * @param blockNumber
//     * @throws BizException
//     */
//    public static BlockGetInfoResult getBlockInfo(Long blockNumber) throws BizException {
//
//        // 初始化请求参数
//        BlockGetInfoRequest request = new BlockGetInfoRequest();
//        request.setBlockNumber(blockNumber);
//
//        // 调用getInfo接口
//        BlockGetInfoResponse response = SDK.getSdk().getBlockService().getInfo(request);
//        if (response.getErrorCode() == 0) {
//            BlockGetInfoResult result = response.getResult();
//            log.info("getTransactions input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            return result;
//        } else {
//            log.error("getBlockInfo input:[{}] ,output:[{}]", JSONUtil.toJsonStr(request), JSONUtil.toJsonStr(response));
//            throw new BizException(ResultCode.BUSINESS_CHAIN_EXECUTION, JSONUtil.toJsonStr(response));
//        }
//    }
//
//    public static void main(String[] args) {
//
//        String account = ChainOperateUtils.createAccount();
//        System.out.println(account);
//
//        String contractAddress = "ZTX3LAWbAWrXaohJwYRfYVRUiyyUJteRkUiPL";
//        String sourceAddress = "ZTX3QoBsFVSwsjKUSDtw2egiUWiYyXgZEGspr";
////        String buildBalanceOfOperation = ChainOperateUtils.buildGetPledgeOperation(ChainOperateUtils.dTHex(1L));
////        ContractCallResult contractCallResult = ChainOperateUtils.queryContract(contractAddress, sourceAddress, buildBalanceOfOperation);
//
//
////        Long lastNumber = ChainOperateUtils.getLastNumber();
////        BlockGetInfoResult blockInfo = ChainOperateUtils.getBlockInfo(lastNumber);
////        BlockGetTransactionsResult blockGetTransactionsResult = ChainOperateUtils.getTransactions(lastNumber);
////
////        TransactionHistory[] transactions = blockGetTransactionsResult.getTransactions();
////        System.out.println(JSONUtil.toJsonStr(transactions));
//
//
//        String buildGetManagerOperation = ChainOperateUtils.buildGetOperatorsOperation();
//        ContractCallResult contractCallResult = ChainOperateUtils.queryContract(contractAddress, sourceAddress, buildGetManagerOperation);
//        JSONArray operators = contractCallResult.getQueryRets().getJSONObject(0).getJSONObject("result").getJSONObject("value").getJSONArray("operators");
//        System.out.println(Lists.newArrayList(operators));
//
////        TransactionHistory[] transactions = ChainOperateUtils.getTransaction("4803fbd8ccf57f245e082d9b7378df3b5ed8321be7c72e3f1127dfb4f72287ca");
////        for (TransactionHistory transactionHistory : transactions) {
////            if (transactionHistory.getErrorCode() == 0) {
////                TransactionInfo transaction = transactionHistory.getTransaction();
////                System.out.println(JSONUtil.toJsonStr(transaction));
////                String sourceAddress = transaction.getSourceAddress();
////                Operation[] operations = transaction.getOperations();
////                for (Operation operation : operations) {
////                    GasSendInfo sendGas = operation.getSendGas();
////                    String destAddress = sendGas.getDestAddress();
////                    String input = sendGas.getInput();
////                    JSONObject jsonObject = JSONUtil.parseObj(input);
////                    String method = jsonObject.getStr("method");
////                    JSONObject params = jsonObject.getJSONObject("params");
////                    String id = params.getStr("id");
////                    System.out.println(method + " " + destAddress + " " + id);
////                }
////            }
////        }
//    }
//}
////{"sourceAddress":"ZTX3WqX9oX6v9TgNT8syfidKpzDv5veh14L4s","nonce":119,"feeLimit":1000000,"operations":[{"sourceAddress":"ZTX3WqX9oX6v9TgNT8syfidKpzDv5veh14L4s","type":7,"sendGas":{"amount":100000,"destAddress":"ZTX3VhHJ96zdy5CLttacCn69XbnbB2NRTPdk7"}}],"gasPrice":1000}
////{"sourceAddress":"ZTX3QoBsFVSwsjKUSDtw2egiUWiYyXgZEGspr","nonce":457,"feeLimit":1500000,"operations":[{"sourceAddress":"ZTX3QoBsFVSwsjKUSDtw2egiUWiYyXgZEGspr","type":7,"sendGas":{"input":"{\"method\": \"withdraw\",\"params\":{\"id\":\"00000000000000000000000000000000000000000000000000034c207fadf863\"}}","destAddress":"ZTX3XQ4qRwqtRCu7ohDxztAnBx6L7SpXMZnTs"}}],"gasPrice":1000}

package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.CardType;
import com.dlc.modules.sys.entity.FitCardEntity;

import java.util.List;
import java.util.Map;

/**
 * 健身卡(会员卡)
 *
 * @author wangsheng
 * @email
 * @date 2018-09-10 10:24:28
 */
public interface SysFitCardService {
    FitCardEntity queryObject(Long fitCardId);

    List<FitCardEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(FitCardEntity fitCardEntity);

    void update(FitCardEntity fitCardEntity);

    // void updates(FitCardEntity fitCardEntity);

    void delete(Long fitcardId);

    void deleteBatch(Long[] fitCardIds);
    Integer fitCardCount(long cardId);

    List<CardType> selectCardTypeList();

    int queryIfExistCardType(String ctName);

    Integer getLastCardType();

    int saveCardType(CardType cardType);

    int updateCardType(CardType cardType);

    int queryIfExistCardTypeByCtId(Long ctId);

    int deleteCardType(Long ctId);

    int updateOnOffCard(Long fitCardId, Integer status);
}


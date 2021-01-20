package com.lisdoo.jstock.service.exchange;

import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class JstockRangeService {

    @Autowired
    JstockRangeRecordRepository jrrr;

    @Autowired
    JstockRangeRepository jrp;

    public JstockRange createJstockRange(JstockRange jr) throws EntityExistException {
        // 为空时，判断是否有重复
        if (StringUtils.isEmpty(jr.getPosition())) {
            Optional<JstockRange> optional = jrp.findByJstockAndParentAndPosition(jr.getJstock(), jr.getParent(), null);
            if (optional.isPresent()) {
                throw new EntityExistException();
            }
            return jrp.save(jr);
        } else {
            Optional<JstockRange> optional = jrp.findByJstockAndParentAndPosition(jr.getJstock(), jr.getParent(), jr.getPosition());
            if (optional.isPresent()) {
                throw new EntityExistException();
            }
            return jrp.save(jr);
        }
    }

    public JstockRange updateJstockRangeStatus(JstockRange jr, Integer position, String state, Data data) throws EntityNoneException {

        if (!jrp.existsById(jr.getId())) {
            throw new EntityNoneException();
        }

        JstockRange pjr = jr.getParent();
        pjr.setLastPosition(position);
        pjr.setLastTradeDate(data.getDateTime());
        jrp.save(pjr);

        Calculation.Status status = Calculation.Status.valueOf(state);

        JstockRangeRecord jrr = new JstockRangeRecord(pjr, jr, 0f, jr.getJstockStrategy(), 0, 0f, null, false, data.getDateTime(), new Date(), null);;

        jr.setStatus(state);
        switch(status) {
            case BUY: {
                jrr.setStatus(Calculation.Status.BUY.name());
                jrr.setPrice(data.getBuyPrice()/100);
            } break;
            case SELL: {
                jrr.setStatus(Calculation.Status.SELL.name());
                jrr.setPrice(data.getSellPrice()/100);
            } break;
        }

        jrrr.save(jrr);

        jr.setLastTradeDate(data.getDateTime());
        return jrp.save(jr);
    }
}

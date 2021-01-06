package com.lisdoo.jstock.service.exchange;

import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
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

    public JstockRange updateJstockRangeStatus(JstockRange jr, Integer position, String state, Date quoteTime) throws EntityNoneException {

        if (!jrp.existsById(jr.getId())) {
            throw new EntityNoneException();
        }

        JstockRange pjr = jr.getParent();
        pjr.setLastPosition(position);
        pjr.setLastTradeDate(new Date());
        jrp.save(pjr);

        Calculation.Status status = Calculation.Status.valueOf(state);

        JstockRangeRecord jrr = new JstockRangeRecord(pjr, jr, jr.getBasePrise(), jr.getJstockStrategy(), 0, 0f, null, false, quoteTime, new Date(), null);;

        jr.setStatus(state);
        switch(status) {
            case BUY: {
                jrr.setStatus(Calculation.Status.BUY.name());
            } break;
            case SELL: {
                jrr.setStatus(Calculation.Status.SELL.name());
            } break;
        }

        jrrr.save(jrr);

        return jrp.save(jr);
    }
}

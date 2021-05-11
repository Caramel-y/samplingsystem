package com.project.samplingsystem.web.management.sampling;

import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.SamplingRepository;
import com.project.samplingsystem.model.entity.Sampling;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * created by chen on 2021/05/11 at 13:15
 *
 * @author chen
 */
@Controller
@RequestMapping("/management/sampling")
public class SamplingController extends BaseController {

    private final SamplingRepository samplingRepository;

    @Autowired
    public SamplingController(SamplingRepository samplingRepository) {
        this.samplingRepository = samplingRepository;
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    @NBAuth(value = "management:sampling:list", remark = "采样页面分页数据", group = NBAuth.Group.AJAX)
    public List<Sampling> listSampling(Pagination<Sampling> catePage, @RequestParam(value = "samplingName") String samplingName) {
        Pageable pageable = PageRequest.of(catePage.getPage() - 1, catePage.getLimit());
        List<Sampling> samplings = samplingRepository.findAll(new Specification<Sampling>() {
            @Override
            public Predicate toPredicate(Root<Sampling> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (StringUtils.isNotBlank(samplingName)) {
                    predicates.add(cb.equal(root.get("sampleName"), samplingName));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        });

        return samplings;
    }

//    @RequestMapping("/list")
//    @ResponseBody
//    @NBAuth(value = "management:cate:list", remark = "分类管理分页数据", group = NBAuth.Group.AJAX)
//    public LayuiTable<NBCate> cateList(Pagination<NBCate> catePage) {
//        //jpa分页是从0开始
//        Pageable pageable = PageRequest.of(catePage.getPage() - 1, catePage.getLimit());
//        Page<NBCate> page = cateRepository.findAll(pageable);
//        return layuiTable(page, pageable);
//    }
}

package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.common.entity.Bookmark;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 15:00
 */
@Service
public class BookmarkService {
    /**
     * chrome导出书签tag
     */
    private static final String DT = "dt";
    private static final String A = "a";

    @Autowired
    private BookmarkDao bookmarkDao;

    @Transactional(rollbackFor = Exception.class)
    public void parseBookmarkFile(InputStream stream, String path) throws Exception {
        Document doc = Jsoup.parse(stream, "utf-8", "");
        Elements elements = doc.select("html>body>dl>dt");
        //获取当前层sort最大值
        Integer sortBase = bookmarkDao.selectMaxSort(1, path);
        if (sortBase == null) {
            sortBase = 0;
        }
        int count = 0;
        for (int i = 0, length = elements.size(); i < length; i++) {
            if (i == 0) {
                Elements firstChildren = elements.get(0).child(1).children();
                count = firstChildren.size();
                for (int j = 0; j < count; j++) {
                    dealBookmark(firstChildren.get(j), path, sortBase + j);
                }
            } else {
                dealBookmark(elements.get(i), path, sortBase + count + i - 1);
            }
        }
    }

    /**
     * Description: 处理html节点，解析出文件夹和书签
     *
     * @param ele  待处理节点
     * @param path 节点路径，不包含自身
     * @param sort 当前层级中的排序序号
     * @author fanxb
     * @date 2019/7/8 14:49
     */
    private void dealBookmark(Element ele, String path, int sort) {
        if (!DT.equalsIgnoreCase(ele.tagName())) {
            return;
        }
        Element first = ele.child(0);
        if (A.equalsIgnoreCase(first.tagName())) {
            //说明为链接
            Bookmark node = new Bookmark(1, path, first.ownText(), first.attr("href"), first.attr("icon")
                    , Long.valueOf(first.attr("add_date")) * 1000, sort);
            //存入数据库
            insertOne(node);
        } else {
            //说明为文件夹
            Bookmark node = new Bookmark(1, path, first.ownText(), Long.valueOf(first.attr("add_date")) * 1000, sort);
            Integer sortBase = 0;
            if (insertOne(node)) {
                sortBase = bookmarkDao.selectMaxSort(node.getUserId(), path);
                if (sortBase == null) {
                    sortBase = 0;
                }
            }
            String childPath = path.length() == 0 ? node.getBookmarkId().toString() : path + "." + node.getBookmarkId();
            Elements children = ele.child(1).children();
            for (int i = 0, size = children.size(); i < size; i++) {
                dealBookmark(children.get(i), childPath, sortBase + i + 1);
            }
        }
    }

    /**
     * Description:
     *
     * @param node node
     * @return boolean 如果已经存在返回true，否则false
     * @author fanxb
     * @date 2019/7/8 17:25
     */
    private boolean insertOne(Bookmark node) {
        //先根据name,userId,parentId获取此节点id
        Integer id = bookmarkDao.selectIdByUserIdAndNameAndPath(node.getUserId(), node.getName(), node.getPath());
        if (id == null) {
            bookmarkDao.insertOne(node);
            return false;
        } else {
            node.setBookmarkId(id);
            return true;
        }
    }
}

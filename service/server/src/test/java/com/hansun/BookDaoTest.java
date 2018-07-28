//package com.hansun;
//
//import com.hansun.server.SpringBootHSApp;
//import com.hansun.server.db.dao.Book;
//import com.hansun.server.db.dao.BookDao;
//import javafx.application.Application;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.Date;
//import java.util.Iterator;
//
///**
// * Created by qianlong on 16/9/27.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = SpringBootHSApp.class)
//public class BookDaoTest {
//
//    @Autowired
//    private BookDao bookDao;
//
////    @Autowired
////    private BookStoeDao bookStoeDao;
//
//    @Test
//    public void testBook(){
//        try{
//            //先清空已有数据
//            bookDao.deleteAll();
//
//            //生成10本书
//            for(int i=1;i<=10;i++){
//                bookDao.save(new Book("book"+i,"author"+i,new Date()));
//            }
//
//            // 测试findAll, 查询所有记录
//            Assert.assertEquals(10, bookDao.findAll().size());
//
//            // 测试findByTitle, 查询书名为book5的书
//            Assert.assertEquals("author5", bookDao.findByTitle("book5").getAuthor());
//
//            // 测试findBook, 查询书名为book7的书
//            Assert.assertEquals("author7", bookDao.findBook("book7").getAuthor());
//
//            // 测试findByTitleAndAuthor, 查询书名为book1,作者为author1的书
//            Assert.assertEquals("author1", bookDao.findByTitleAndAuthor("book1","author1").getAuthor());
//
//            // 测试findByTitleAndAuthor, 查询书名为book2,作者为author1的书
//            Assert.assertEquals(null, bookDao.findByTitleAndAuthor("book2", "author1"));
//
//            //测试删除book3
//            bookDao.delete(bookDao.findBook("book3"));
//
//            //测试删除是否成功
//            Assert.assertEquals(9, bookDao.findAll().size());
//
//            //分页查询
//            bookDao.deleteAll();
//            for(int i=1;i<=10;i++){
//                bookDao.save(new Book("book"+i,"Alex Qian",new Date()));
//            }
//            Sort sort = new Sort(Sort.Direction.DESC, "bookId");
//            int page = 1;
//            int size = 5;
//            Pageable pageable = new PageRequest(page, size, sort);
//            Page<Book> pages = bookDao.findBookPage(pageable,"Alex Qian");
//
//            Iterator<Book> it= pages.iterator();
//
//            Assert.assertEquals(size,pages.getSize());
//            Assert.assertEquals(2,pages.getTotalPages());
//
//            while(it.hasNext()){
//                Book book = (Book)it.next();
//                System.out.println("title/author/createTime:"+book.getTitle()+"/"+book.getAuthor()+"/"+book.getCreateTime());
//            }
//
//        }catch (Exception ex){
//            Assert.fail(ex.getMessage());
//        }
//    }
////
////    @Test
////    public void testBookStore(){
////        try{
////            bookStoeDao.deleteAll();
////
////            //生成5个书店
////            for(int i=1;i<=5;i++){
////                bookStoeDao.save(new BookStore("address_"+i,"manager_"+i));
////            }
////            // 测试findAll, 查询所有记录
////            Assert.assertEquals(5, bookStoeDao.findAll().size());
////            Assert.assertEquals(1, bookStoeDao.getBookStoreByManager("manager_2").size());
////            Assert.assertEquals("manager_2", bookStoeDao.getBookStoreByManager("manager_2").get(0).getStoreManager());
////
////        }catch (Exception ex){
////            Assert.fail(ex.getMessage());
////        }
////    }
//
//}
//

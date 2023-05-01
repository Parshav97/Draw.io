/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.newsfeedapplication;
import java.util.*;
import java.lang.Math;
import java.time.*;
import java.time.temporal.ChronoUnit;
/**
 *
 * @author Asus
 */
public class NewsFeedApplication{
    static int size = (int)1e5;
    static int tokenSize = (int)1e8;
    static HashMap<String, Users> sessionDB = new HashMap<>();
    static HashMap<String, Users> usersDB = new HashMap<>();
    static HashMap<String, Posts> postsDB = new HashMap<>();
    static HashMap<String, Comments> commentsDB = new HashMap<>();
    static HashSet<String> posts_upvotes = new HashSet<>();
    static HashSet<String> posts_downvotes = new HashSet<>();
    static HashSet<String> comments_upvotes = new HashSet<>();
    static HashSet<String> comments_downvotes = new HashSet<>();
    static HashSet<String> followersDB = new HashSet<>();
    
    

        
    
   
    class Users{
        public String firstName;
        public String secondName;
        public String username;
        private String password;
        private String token;
        public ArrayList<Users> followers;
        public ArrayList<Users> followings;
        public ArrayList<Posts> posts;
        
        Users(String fname, String sname, String userName, String pass){
            this.firstName = fname;
            this.secondName = sname;
            this.username = userName;
            this.password = pass;
            this.token = "";
            this.followers = new ArrayList<Users>();
            this.followings = new ArrayList<Users>();
            this.posts = new ArrayList<Posts>();
        }
        public void setToken(String newToken){
            this.token = newToken;
        }
        public String getToken(){
            return this.token;
        }
        public String getPasword(){
            return this.password;
        }
        public Posts post(String text){
            Posts p = new Posts(text, this.username);
            this.posts.add(p);
            postsDB.put(p.id, p);
            return p;
        }
        public ArrayList<Users> showUsers(){
            ArrayList<Users> notfollowedUsers = new ArrayList<>();
            for(Users user : usersDB.values()){
                if(user.username.equals(this.username)){
                    continue;
                }
                if(!this.followings.contains(user)){
                    notfollowedUsers.add(user);
                }
            }
            return notfollowedUsers;
        }
        public ArrayList<Posts> showPosts(){
            return this.posts;
        }
        public ArrayList<Users> showFollowers(){
            return this.followers;
        }
        public ArrayList<Users> showFollowings(){
            return this.followings;
        }
        public ArrayList<Comments> showComments(String postId){
            if(!postsDB.containsKey(postId)){
                System.out.println("Invalid Post Id");
                return new ArrayList<Comments>();
            }            
            return postsDB.get(postId).comments;
        }
        public boolean follow(String username){
            if(this.username.equals(username)){
                System.out.println(this.username+" cannot follow itself");
                return false;
            }
            if(!usersDB.containsKey(username)){
                System.out.println("Incorrect Arguments Provided");
                return false;
            }
            String str = this.username+"="+username;
            if(followersDB.contains(str)){
                System.out.println(this.username +" already follows " + username);
                return false;
            }
            followersDB.add(str);
            return this.followings.add(usersDB.get(username)) && usersDB.get(username).followers.add(this);
        }
        public boolean reply(String postId, String text){
            if(!postsDB.containsKey(postId)){
                System.out.println("Invalid Post Id");
                return false;
            }
            Comments newComment = new Comments(text, this.username);
            postsDB.get(postId).comments.add(newComment);
            commentsDB.put(newComment.id, newComment);
            return true;
        }
        public boolean upvote(String postId){
            if(!postsDB.containsKey(postId)){
                System.out.println("Invalid Post Id");
                return false;
            }
            String str = ""+postId+"="+this.username;
            Posts postObj = postsDB.get(postId);
            if(posts_downvotes.contains(str)){
                posts_downvotes.remove(str);
            }
            if(postObj.downvotes.contains(this.username)){
                postObj.downvotes.remove(this.username);
            }
            posts_upvotes.add(str);
            postObj.upvotes.add(this.username);
            return true;
        }
        public boolean downvote(String postId){
            if(!postsDB.containsKey(postId)){
                System.out.println("Invalid Post Id");
                return false;
            }
            String str = ""+postId+"="+this.username;
            Posts postObj = postsDB.get(postId);
            if(postObj.upvotes.contains(this.username)){
                postObj.upvotes.remove(this.username);
            }
            if(posts_upvotes.contains(str)){
                posts_upvotes.remove(str);
            }
            posts_downvotes.add(str);
            postObj.downvotes.add(this.username);
            return true;
        }
        public ArrayList<Posts> showNewsFeed(){
            ArrayList<Posts> feed = new ArrayList<>();
            Iterator it = this.followings.iterator();
            while(it.hasNext()){
               Users userObj = (Users)(it.next());
               feed.addAll(userObj.posts);
            }
            Collections.sort(feed, new Custom());
            return feed;
        } 
        public boolean commentOnComment(String commentId, String text){
            if(!commentsDB.containsKey(commentId)){
                System.out.println("Invalid Comment Id");
                return false;
            }
            Comments comm = new Comments(text, this.username);
            String temp = commentId;
            while(commentsDB.get(temp).response!=null){
                temp = commentsDB.get(temp).response.id;
            }
            commentsDB.get(temp).response = comm;
            commentsDB.put(comm.id, comm);
            return true;
        }
        public boolean upvoteComment(String commentId){
            if(!commentsDB.containsKey(commentId)){
                System.out.println("Invalid Comment Id");
                return false;
            }
            String str = ""+commentId+"="+this.username;
            Comments obj = commentsDB.get(commentId);
            if(comments_downvotes.contains(str)){
                comments_downvotes.remove(str);
            }
            if(obj.downvotes.contains(this.username)){
                obj.downvotes.remove(this.username);
            }
            obj.upvotes.add(this.username);
            comments_upvotes.add(str);
            return true;
        }
        public boolean downvoteComment(String commentId){
            if(!commentsDB.containsKey(commentId)){
                System.out.println("Invalid Comment Id");
                return false;
            }
            String str = ""+commentId+"="+this.username;
            Comments obj = commentsDB.get(commentId);
            if(comments_upvotes.contains(str)){
                comments_upvotes.remove(str);
            }
            if(obj.upvotes.contains(this.username)){
                obj.upvotes.remove(this.username);
            }
            commentsDB.get(commentId).downvotes.add(this.username);
            comments_upvotes.add(str);
            return true;
        }
        
                
        public void logout(){
            this.token = "";
        }
    }
    class Custom implements Comparator<Posts>{
        @Override
        public int compare(Posts p1, Posts p2){
            int votesMag1 = p1.upvotes.size() - p1.downvotes.size();
            int votesMag2 = p2.upvotes.size() - p2.downvotes.size();
            int commMag1 = p1.comments.size();
            int commMag2 = p2.comments.size();

            int totalSum =  votesMag1 + commMag1;
            int totalSum1 =  votesMag2 + commMag2;

            LocalDateTime from = p1.datetime;
            LocalDateTime to = LocalDateTime.now();

            long years = ChronoUnit.YEARS.between(from, to);
            long months = ChronoUnit.MONTHS.between(from, to) - years*12;
            long weeks = ChronoUnit.WEEKS.between(from, to) - (long)(((double)months/(double)12)*52) - years*12;
            long days = ChronoUnit.DAYS.between(from, to) - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
            long hours = ChronoUnit.HOURS.between(from, to) - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
            long minutes = ChronoUnit.MINUTES.between(from, to) - hours*60 - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
            long seconds = ChronoUnit.SECONDS.between(from, to) - minutes*60 - hours*60 - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;


            LocalDateTime from1 = p2.datetime;
            LocalDateTime to1 = LocalDateTime.now();
            
            long years1 = ChronoUnit.YEARS.between(from, to);
            long months1 = ChronoUnit.MONTHS.between(from, to) - years1*12;
            long weeks1 = ChronoUnit.WEEKS.between(from, to) - (long)(((double)months1/(double)12)*52) - years1*12;
            long days1 = ChronoUnit.DAYS.between(from, to) - weeks1*7 - (long)(((double)months1/(double)12)*52) - years1*12;
            long hours1 = ChronoUnit.HOURS.between(from, to) - days1*24 - weeks1*7 - (long)(((double)months1/(double)12)*52) - years1*12;
            long minutes1 = ChronoUnit.MINUTES.between(from, to) - hours1*60 - days1*24 - weeks1*7 - (long)(((double)months1/(double)12)*52) - years1*12;
            long seconds1 = ChronoUnit.SECONDS.between(from, to) - minutes1*60 - hours1*60 - days1*24 - weeks1*7 - (long)(((double)months1/(double)12)*52) - years1*12;

            if(years == years1){
                if(months== months1){
                    if(weeks == weeks1){
                         if(days == days1){
                             if(hours == hours1){
                                 if(minutes == minutes1){
                                     return totalSum1 - totalSum;
                                  }else{
                                     if(totalSum > totalSum1 && minutes < minutes1){
                                         return -1;
                                     }else if(totalSum < totalSum1 && minutes > minutes1){
                                         return 1;
                                     }
                                     return (int)((totalSum + minutes) - (totalSum1 + minutes1));
                                  }
                             }else{
                                if(totalSum > totalSum1 && hours < hours1){
                                    return -1;
                                }else if(totalSum < totalSum1 && hours > hours1){
                                    return 1;
                                }
                                return (int)((totalSum + hours) - (totalSum1 + hours1));
                             }
                         }else{
                            if(totalSum > totalSum1 && days < days1){
                                return -1;
                            }else if(totalSum < totalSum1 && days > days1){
                                return 1;
                            }
                            return (int)((totalSum + days)-(totalSum1 + days1));
                         }
                    }else{
                        if(totalSum > totalSum1 && weeks < weeks1){
                            return -1;
                        }else if(totalSum < totalSum1 && weeks > weeks1){
                            return 1;
                        }
                        return (int)((totalSum + weeks) - (totalSum1 + weeks1));
                    }
                 }else{
                    if(totalSum > totalSum1 && months < months1){
                        return -1;
                    }else if(totalSum < totalSum1 && months > months1){
                        return 1;
                    }
                    return (int)((totalSum + months)-(totalSum1 + months1));
                 }
            }else{
                if(totalSum > totalSum1 && years < years1){
                    return -1;
                }else if(totalSum < totalSum1 && years > years1){
                    return 1;
                }
                return (int)((totalSum + years) - (totalSum1 + years1));
            }
        }
    }
    class Posts{
        public String id;
        public String postedBy;
        public LocalDateTime datetime;
        public HashSet<String> upvotes;
        public HashSet<String> downvotes;
        public String content;
        public ArrayList<Comments> comments;
        
        Posts(String text, String username){
           this.content = text;
           this.id = String.valueOf((int)(Math.random()*size));
           this.postedBy = username;
           this.datetime = LocalDateTime.now();
           this.upvotes = new HashSet<String>();
           this.downvotes = new HashSet<String>();
           this.comments = new ArrayList<Comments>();
        }
    }
    class Comments{
        public String id;
        public String commentedBy;
        public String content;
        public HashSet<String> upvotes;
        public HashSet<String> downvotes;
        public Comments response;
        
        Comments(String text, String username){
            this.id = String.valueOf((int)(Math.random()*size));
            this.content = text;
            this.commentedBy = username;
            this.upvotes = new HashSet<>();
            this.downvotes = new HashSet<>();
            this.response = null;
        }
    }
    
    public Users signup(String fname, String sname, String username, String pass){
        if(usersDB.containsKey(username)){
            System.out.println(username+" already exist in database, pls try with a different username");
            return null;
        }
        Users userOne = new Users(fname, sname, username, pass);
        usersDB.put(username, userOne);
        return userOne;
    }
    public String login(String username, String pass){
        if(!usersDB.containsKey(username)){
            return null;
        }
        if(usersDB.containsKey(username) && !usersDB.get(username).getPasword().equals(pass)){
            return null;
        }
        String token = String.valueOf((int)(Math.random()*tokenSize));
        Users userObject = usersDB.get(username);
        userObject.setToken(token);
        sessionDB.put(token, userObject);
        return token;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        NewsFeedApplication session = new NewsFeedApplication();
        //Fake Database
        Users user1 = session.new Users("parshav","nahta","parshav97","abc");
        Users user2 = session.new Users("aman","mittal","aman98","abc");
        Users user3 = session.new Users("sonia","khanna","sonia99","abc");
        usersDB.put("parshav97",user1);
        usersDB.put("aman98",user2);
        usersDB.put("sonia99",user3);
        
        Posts p1 = session.new Posts("Today is the labour day","parshav97");
        Posts p2 = session.new Posts("We are chilling in goa","parshav97");
        Posts p3 = session.new Posts("Best day of the year","parshav97");
        user1.posts.add(p1);
        user1.posts.add(p2);
        user1.posts.add(p3);

        Posts p4 = session.new Posts("Good Evening, having a great day","aman98");
        Posts p5 = session.new Posts("Good Afternoon, doing great","aman98");
        Posts p6 = session.new Posts("Good Morning, what a wonderful day","aman98");
        user2.posts.add(p4);
        user2.posts.add(p5);
        user2.posts.add(p6);
        
        Posts p7 = session.new Posts("Busy lot of work to do","sonia99");
        Posts p8 = session.new Posts("Learning New Things in new project","sonia99");
        Posts p9 = session.new Posts("Amazing Work at office","sonia99");
        user3.posts.add(p7);
        user3.posts.add(p8);
        user3.posts.add(p9);
        
        postsDB.put(p1.id, p1);
        postsDB.put(p2.id, p2);
        postsDB.put(p3.id, p3);
        postsDB.put(p4.id, p4);
        postsDB.put(p5.id, p5);
        postsDB.put(p6.id, p6);
        postsDB.put(p7.id, p7);
        postsDB.put(p8.id, p8);
        postsDB.put(p9.id, p9);
        
        user1.follow("aman98");
        user1.follow("sonia99");
        user2.follow("parshav97");
        user2.follow("sonia99");
        user3.follow("parshav97");
        user3.follow("aman98");
        followersDB.add(user1.username+"=aman98");
        followersDB.add(user1.username+"=sonia99");
        followersDB.add(user2.username+"=parshav97");
        followersDB.add(user2.username+"=sonia99");
        followersDB.add(user3.username+"=aman98");
        followersDB.add(user3.username+"=parshav97");

        
        Comments c1 = session.new Comments("Great Enjoy","aman98");
        Comments c2 = session.new Comments("Have a Blast","sonia99");
        Comments c3 = session.new Comments("when did u went","aman98");
        Comments c4 = session.new Comments("let me know when u r back","sonia99");
        
        commentsDB.put(c1.id,c1);
        commentsDB.put(c2.id,c2);
        commentsDB.put(c3.id,c3);
        commentsDB.put(c4.id,c4);
        
        p2.comments.add(c1);
        p2.comments.add(c2);
        p2.comments.add(c3);
        p2.comments.add(c4);
        
        user1.upvote(p4.id);
        posts_upvotes.add(p4.id+"="+user1.username);
        user2.upvote(p4.id);
        posts_upvotes.add(p4.id+"="+user2.username);
        user3.upvote(p4.id);
        posts_upvotes.add(p4.id+"="+user3.username);
        user1.upvote(p8.id);
        posts_upvotes.add(p8.id+"="+user1.username);
        user2.upvote(p8.id);
        posts_upvotes.add(p8.id+"="+user2.username);
        user3.downvote(p1.id);
        posts_downvotes.add(p1.id+"="+user3.username);
        
        Comments c5 = session.new Comments("i wil be back by end of the week","parshav97");
        commentsDB.put(c5.id,c5);
        c4.response = c5;
        
        c1.upvotes.add(user1.username);        
        c2.upvotes.add(user3.username);
        c3.downvotes.add(user1.username);
        c3.downvotes.add(user3.username);
        comments_upvotes.add(c1.id+"="+user1.username);
        comments_upvotes.add(c2.id+"="+user3.username);
        comments_downvotes.add(c3.id+"="+user1.username);
        comments_downvotes.add(c3.id+"="+user3.username);
        
        
        // Fake Database ends        

                
        System.out.println("Welcome to The NewsFeed App ");
        System.out.println();
        while(true){
            System.out.println("-----------------------------------------------------------");
            System.out.println("|Write one of below commands to perform action.           |");
            System.out.println("-----------------------------------------------------------");
            System.out.println("|signup(first_name,second_name,unique_user_name,password) |");
            System.out.println("|login(username,password)                                 |");
            System.out.println("|kill()                                                   |");
            System.out.println("-----------------------------------------------------------");

            System.out.print(">>> ");
            String input = s.nextLine();
            input = input.trim().replaceAll(" +","");
            input = input.replaceAll("\"","");
            input = input.replaceAll("\'","");
            int argumentsStartBracket = input.indexOf('(');
            int argumentsEndBracket = input.indexOf(')');
            String command = input;
            if(argumentsStartBracket!=-1 && argumentsEndBracket!=-1){
                command = input.substring(0,argumentsStartBracket);
            }else{
                System.out.println("RESULT");
                System.out.println("-----------------------------------------");
                System.out.println("|Wrong Input Entered                    |");
                System.out.println("-----------------------------------------");
                continue;
            }
            if(command.equals("signup")){
                String[] arguments = input.substring(argumentsStartBracket+1,argumentsEndBracket).split(",");
                if(arguments.length != 4){
                    System.out.println("RESULT");
                    System.out.println("-----------------------------------------");
                    System.out.println("|Invalid arguments provided             |");
                    System.out.println("-----------------------------------------");
                    continue;
                }
                System.out.println("RESULT");
                System.out.println("------------------------------------------------------");
                Users obj = session.signup(arguments[0], arguments[1], arguments[2], arguments[3]);
                if(obj != null){
                    System.out.println(  "|User "+obj.username+" created successfully.         |");
                }
                System.out.println("------------------------------------------------------");

            }else if(command.equals("login")){
                String[] arguments = input.substring(argumentsStartBracket+1,argumentsEndBracket).split(",");
                if(arguments.length != 2){
                    System.out.println("RESULT");
                    System.out.println("-----------------------------------------");
                    System.out.println("|Invalid arguments provided             |");
                    System.out.println("-----------------------------------------");
                }else{
                    String token = session.login(arguments[0], arguments[1]);
                    if(token == null){
                        System.out.println("RESULT");
                        System.out.println("-----------------------------------------");
                        System.out.println("|Invalid arguments provided             |");
                        System.out.println("-----------------------------------------");
                    }else{
                        Users userObj = session.sessionDB.get(token);
                        System.out.println("RESULT");
                        System.out.println("-----------------------------------------------");
                        System.out.println(userObj.username + " successfully logged in.  ");
                        System.out.println("-----------------------------------------------");

                        while(token.equals(userObj.getToken())){
                            System.out.println("--------------------------------------------------------------------");
                            System.out.println("|Write one of below commands to perform action.                    |");
                            System.out.println("--------------------------------------------------------------------");
                            System.out.println("|post(write_your_content_here)                                     |");
                            System.out.println("|showFollowers()                                                   |");
                            System.out.println("|showFollowings()                                                  |");
                            System.out.println("|showUsers()                                                       |");
                            System.out.println("|follow(username)                                                  |");
                            System.out.println("|showNewsFeed()                                                    |");
                            System.out.println("|reply(post_id,write_your_comment_here)                            |");
                            System.out.println("|upvote(post_id)                                                   |");
                            System.out.println("|downvote(post_id)                                                 |");
                            System.out.println("|showComments(post_id)                                             |");
                            System.out.println("|commentOnComment(comment_id,write_your_comment_here)              |");
                            System.out.println("|upvoteComment(comment_id)                                         |");
                            System.out.println("|downvoteComment(comment_id)                                       |");
                            System.out.println("|logout()                                                          |");
                            System.out.println("--------------------------------------------------------------------");


                            System.out.print(">>> ");
                            String input1 = s.nextLine();
                            System.out.println();

                            input1 = input1.trim();
                            int argumentsStartBracket1 = input1.indexOf('(');
                            int argumentsEndBracket1 = input1.indexOf(')');
                            String command1 = input1;
                            if(argumentsStartBracket1!=-1 && argumentsEndBracket1!=-1){
                                command1 = input1.substring(0,argumentsStartBracket1);
                            }else{
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------");
                                System.out.println("|Wrong Input Entered                    |");
                                System.out.println("-----------------------------------------");
                                continue;
                            }
                            if(command1.equals("post")){
                                String postContent = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                Posts postObj = userObj.post(postContent);
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------");
                                System.out.println(postObj.id+" posted Successully");
                                System.out.println("-----------------------------------------");
                            }else if(command1.equals("showFollowers")){
                                ArrayList<Users> list = userObj.showFollowers();
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------------------------------------");
                                for(Users eachUser : list){
                                    System.out.print(eachUser.username+" | ");
                                }
                                System.out.println();
                                System.out.println(list.size()+" followers.");
                                System.out.println("-----------------------------------------------------------------------");

                            }else if(command1.equals("showFollowings")){
                                ArrayList<Users> list = userObj.showFollowings();
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------------------------------------");
                                for(Users eachUser : list){
                                    System.out.print(eachUser.username+" | ");
                                }
                                System.out.println();
                                System.out.println(list.size()+" followings.");
                                System.out.println("-----------------------------------------------------------------------");

                            }else if(command1.equals("showUsers")){
                                ArrayList<Users> list = userObj.showUsers();
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------------------------------------");
                                for(Users eachUser : list){
                                    System.out.print(eachUser.username+" | ");
                                }
                                System.out.println();
                                System.out.println(list.size()+" users.");
                                System.out.println("-----------------------------------------------------------------------");

                            }else if(command1.equals("follow")){
                                String usernameToFollow = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                boolean status = userObj.follow(usernameToFollow);
                                System.out.println("RESULT");
                                System.out.println("-----------------------------------------------------------------------");
                                if(!status){
                                    System.out.println(usernameToFollow + " is not followed by "+ userObj.username);
                                }else{
                                    System.out.println(usernameToFollow + " is followed by "+ userObj.username);
                                }
                                System.out.println("-----------------------------------------------------------------------");

                            }else if(command1.equals("showNewsFeed")){
                                ArrayList<Posts> feed = userObj.showNewsFeed();
                                ArrayList<Posts> temp = new ArrayList<>(userObj.posts);
                                Collections.reverse(temp);
                                feed.addAll(temp);
                                LocalDateTime to = LocalDateTime.now();
                                System.out.println("RESULT");
                                System.out.println("---------------------------------------------------------------------------------------------");
                                
                                for(int k=0;k<feed.size();k++){
                                    System.out.println(feed.get(k).id);
                                    System.out.println(feed.get(k).content);
                                    System.out.println(feed.get(k).postedBy + " | "+feed.get(k).upvotes.size()+" + "+" | "+feed.get(k).downvotes.size()+" - ");
                                    LocalDateTime from = feed.get(k).datetime;
                                    long years = ChronoUnit.YEARS.between(from, to);
                                    long months = ChronoUnit.MONTHS.between(from, to) - years*12;
                                    long weeks = ChronoUnit.WEEKS.between(from, to) - (long)(((double)months/(double)12)*52) - years*12;
                                    long days = ChronoUnit.DAYS.between(from, to) - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
                                    long hours = ChronoUnit.HOURS.between(from, to) - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
                                    long minutes = ChronoUnit.MINUTES.between(from, to) - hours*60 - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12;
                                    long seconds = ChronoUnit.SECONDS.between(from, to) - minutes*60 - hours*60 - days*24 - weeks*7 - (long)(((double)months/(double)12)*52) - years*12; ;
                                    String timeString = new String();
                                    if(years!=0){
                                        timeString += years + " years ";
                                    }
                                    if(months!=0){
                                        timeString += months + " months ";
                                    }
                                    if(weeks!=0){
                                        timeString += weeks + " weeks ";
                                    }
                                    if(days!=0){
                                        timeString += days + " days ";
                                    }
                                    if(hours!=0){
                                        timeString += hours + " hours ";
                                    }
                                    if(minutes!=0){
                                        timeString += minutes + " minutes ";
                                    }
                                    if(seconds!=0){
                                        timeString += seconds + " seconds ";
                                    }
                                    timeString += "ago";
                                    System.out.println(timeString);
                                    System.out.println("-------------------------------------");
                                }
                                System.out.println(feed.size()+" posts are there");
                                System.out.println("---------------------------------------------------------------------------------------------");

                            }else if(command1.equals("reply")){
                                String[] argumentsReply = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1).split(",");
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.reply(argumentsReply[0], argumentsReply[1]);
                                if(status){
                                    System.out.println("Comment on the post added successully!                ");
                                }
                                System.out.println("----------------------------------------------------------");

                            }else if(command1.equals("upvote")){
                                String argumentsUpvote = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.upvote(argumentsUpvote);
                                if(status){
                                    System.out.println("Post Upvoted successully!");
                                }
                                System.out.println("----------------------------------------------------------");

                            }else if(command1.equals("downvote")){
                                String argumentsDownvote = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.downvote(argumentsDownvote);
                                if(status){
                                    System.out.println("Post Downvoted successully!");
                                }
                                System.out.println("----------------------------------------------------------");

                            }else if(command1.equals("showComments")){
                                String argumentsShowComments = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                ArrayList<Comments> comments = userObj.showComments(argumentsShowComments);
                                System.out.println("RESULT");
                                System.out.println("--------------------------------------------------------------------------------");
                                System.out.println("Showing Comments of "+argumentsShowComments+" post id");
                                for(Comments each : comments){
                                    System.out.println(each.id);
                                    System.out.println(each.commentedBy + " | "+each.upvotes.size()+" + "+" | "+each.downvotes.size()+" - ");
                                    System.out.println(each.content);
                                    while(each.response!=null){
                                        each = each.response;
                                        System.out.println("--------------------------------------------");
                                        System.out.println(each.id);
                                        System.out.println(each.commentedBy + " | "+each.upvotes.size()+" + "+" | "+each.downvotes.size()+" - ");
                                        System.out.println(each.content);
                                        System.out.println("--------------------------------------------");
                                    }
                                    System.out.println("*******************************************************");
                                }
                                System.out.println("--------------------------------------------------------------------------------");

                            }else if(command1.equals("commentOnComment")){
                                String[] commentArguments = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1).split(",");
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.commentOnComment(commentArguments[0], commentArguments[1]);
                                if(status){
                                    System.out.println("Comment on the comment added successully!");
                                }
                                System.out.println("----------------------------------------------------------");

                            }else if(command1.equals("upvoteComment")){
                                String commentUpvote = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.upvoteComment(commentUpvote);
                                if(status){
                                    System.out.println("Comment Upvoted successully!");
                                }
                                System.out.println("----------------------------------------------------------");

                            }else if(command1.equals("downvoteComment")){
                                String commentDownvote = input1.substring(argumentsStartBracket1+1,argumentsEndBracket1);
                                System.out.println("RESULT");
                                System.out.println("----------------------------------------------------------");
                                boolean status = userObj.downvoteComment(commentDownvote);
                                if(status){
                                    System.out.println("Comment Downvoted successully!");
                                }
                                System.out.println("----------------------------------------------------------");
                            }else if(command1.equals("logout")){
                                userObj.logout();
                            }
                        }
                    }
                }
            }else if(command.equals("kill")){
                System.out.println("Program Terminated");
                break;
            }
        }
    }
}

import React, {useState, Component} from 'react';
import {StyleSheet, View, Text,TextInput, Button, ScrollView, SafeAreaView, Pressable} from 'react-native';
import TcpSocket from "react-native-tcp-socket";
import utf8 from 'utf8';
import {Buffer} from 'buffer';
import LinearLayout from "./components/LinearLayout"
import OtherLayout from "./components/OtherLayout"


var client;
var length_bitmap;
var buffer_cur;
var Acc_or_Han;
var cur_id;
var buffer;
var socket_buffer;
var UI_List_Buffer = new Array();

class App extends Component {
    constructor(props){
        super(props);
        this._Connect_to_Server();
        Acc_or_Han = 0;
        buffer = null;
        socket_buffer = null;
    };
    state = {
        UIList: new Array(),
        LayoutList : new Array()
    };
    _Connect_to_Server = (bufer) => {
        client = TcpSocket.createConnection({host: "192.168.0.9", port: 5673}, ()=>{
            console.log("connection established");
        });
        client.on('data', (data) => this.checkData(data));
        
    };

    // accumulatedata(data) {
    //     console.log(Date.now()," : ","this is accumulatedata");
    //     let tempArr = this.state.UIList;
    //     tempArr.forEach(function (targetUI){
    //         if(cur_id == targetUI.ID){
    //             let cur_bitmap = data.toString('utf8',0,data.length);
    //             //console.log(targetUI.Bitmap.length);
    //             //console.log("cur bitmap = ",cur_bitmap);
    //             var temp = targetUI.Bitmap.concat(cur_bitmap);
    //             targetUI.Bitmap = temp;
    //             //targetUI.Bitmap += cur_bitmap;
    //             //console.log(targetUI.Bitmap.length);
    //             buffer_cur += data.length;
    //         }
    //     });
    //     // console.log("UIList ",this.state.UIList[0]);
    //     this.setState({
    //         UIList: tempArr
    //     });
    //     // console.log("UIList", this.state.UIList[0]);
    //     if(buffer_cur == length_bitmap)
    //     {
    //         Acc_or_Han = 0;
    //     }
    // }
    checkData(data) {
        console.log(Date.now()," : ","this is checkData",data.length);

        if(socket_buffer == null)
        {
            //console.log(Date.now()," : ","checkData : socket_buffer empty");
            socket_buffer = Buffer.from(data);
        }
        else
        {
            //console.log(Date.now()," : ","checkData : socket_buffer not empty");
            //console.log("socket_buffer length : ",socket_buffer.length);
            var temp_buffer = Buffer.concat([socket_buffer,data]);
            //console.log("socket_buffer length : ",socket_buffer.length);
            socket_buffer = Buffer.from(temp_buffer);
            //console.log("socket_buffer length : ",socket_buffer.length);
        }

        if(socket_buffer.length < 4)
        {
            // console.log(Date.now()," : ","checkData : data length below 4 byte");
            return;
        }
        else
        {
            var target_length = socket_buffer.readUInt32BE(0);
            var rest_length = socket_buffer.length - 4;
            console.log(Date.now()," : ","checkData : target_length : ", target_length);
            // console.log(Date.now()," : ","checkData : rest_length : ", rest_length);
            if(target_length == rest_length)
            {
                // console.log(Date.now()," : ","checkData : target_length met");
                target_Data = socket_buffer.subarray(4,4+target_length);
                this.handleData(target_Data);
                socket_buffer = null;
                return;
            }
            else if(target_length > rest_length)
            {
                // console.log(Date.now()," : ","checkData : target_length unmet");

                return;
            }
            else //target_length > rest_length
            {
                // console.log(Date.now()," : ","checkData : target_length exceed");
                var target_Data = socket_buffer.subarray(4,4+target_length);
                var rest_Data = socket_buffer.subarray(4+target_length,socket_buffer.length);
                this.handleData(target_Data);
                socket_buffer = Buffer.from(rest_Data);
                this.checkData(Buffer.alloc(0));
                return;
            }
        }
    }
    // parseData(data) {
    //     console.log("this is parseData");
    //     console.log("data length : ",data.length);
    //     // console.log(data);
    //     if(data.length < 4)
    //     {
    //         buffer = new Buffer(data);
    //     }
    //     else
    //     {
    //         if(buffer != null)
    //         {
    //             console.log("parseData : append to buffer");
    //             var temp = Buffer.concat([buffer,data]);
    //             buffer = null;
    //             data = Buffer.from(temp);
    //             console.log(data);
    //         }
           
    //         if(Acc_or_Han == 1)
    //         {
    //             console.log("parseData : accumulate data");
    //             this.accumulatedata(data);
    //         }
    //         else{
    //             console.log("parseData : split and pass to handleData");
    //             var packet_length = data.readUInt32BE(0);
    //             if(packet_length == data.length-4)
    //             {
    //                 var first_data = data.subarray(4,4+packet_length);
    //                 this.handleData(first_data);
    //             }
    //             else if(packet_length < data.length-4) //data longer than expected
    //             {
    //                 console.log("packet length : ",packet_length);
                
    //                 var first_data = data.subarray(4,4+packet_length);
    //                 var second_data = data.subarray(8+packet_length,data.length);
    //                 this.handleData(first_data);
    //                 this.handleData(second_data);
    //             }
    //             else //data shorter than expected
    //             {
    //                 if(socket_buffer == null)
    //                 {
    //                     socket_buffer = Buffer.from(data);
    //                 }
    //                 else
    //                 {
    //                     var temp_buffer = Buffer.concat([socket_buffer,data]);
    //                     socket_buffer = Buffer.from(temp_buffer);
    //                 }
    //             }
    //         }
    //     }
        
    // }
    handleData(data) {
        console.log(Date.now()," : ","handle data invocated");
        
        // console.log(Date.now()," : ","data length : ",data.length);
        //var this_data = data;
        var offset = 0;
    
        //console.log('message received',data);
        console.log(Date.now()," : ",'id : ',offset, data.readUInt32BE(offset));
        var id = data.readUInt32BE(offset);
        offset += 4;
        var isUpdate = data.readUInt32BE(offset);
        // console.log(Date.now()," : ",'isUpdate : ',isUpdate);
        offset +=4;
        if(isUpdate == 0) //distribute mode
        {
            //read WidgetType
            var layoutId = data.readUInt32BE(offset);
            offset+=4;

            var X = data.readFloatBE(offset);
            offset+=4;
            var Y = data.readFloatBE(offset);
            offset+=4;

            // console.log('String Length',offset,data.readUInt32BE(offset));
            var stringSize = data.readUInt32BE(offset)+2;
            offset += 4;
            // console.log('widgetType : ',offset,data.toString('utf8',offset,offset+stringSize));
            var widgetType = data.toString('utf8',offset,offset+stringSize);
            offset += stringSize;
            // console.log("check : ",widgetType,widgetType.includes("EditText"));
            if(widgetType.includes("EditText")==1)
            {
                console.log("handleData : creating EditText data");
                // console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                // console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                // console.log('TextSize : ',data.readFloatBE(offset));
                var TextSize = data.readFloatBE(offset);
                offset += 4;
                var height = data.readUInt32BE(offset)*0.728;
                offset +=4;
                var width = data.readUInt32BE(offset)*0.728;
                offset +=4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "TextSize": TextSize,
                    "Color": 'black',
                    "Parent_ID": layoutId,
                    "X": X,
                    "Y": Y,
                    "Height": height,
                    "Width": width,
                };

                UI_List_Buffer.push(UIdata);
                // let tempArr = this.state.UIList;
                // tempArr.push(UIdata);
                // console.log("UIdata : ",UIdata);

                // UI_List_Buffer = tempArr;
                // this.setState({
                //     UIList: tempArr
                // });
            }
            else if (widgetType.includes("TextView"))
            {
                console.log(Date.now()," : ","handleData : creating TextView data");
                // console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                // console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                // console.log('TextSize : ',data.readFloatBE(offset));
                var TextSize = data.readFloatBE(offset);
                offset += 4
                var height = data.readUInt32BE(offset)*0.728;
                offset +=4;
                var width = data.readUInt32BE(offset)*0.728;
                offset +=4;
                var hasImage = data.readUInt32BE(offset);
                offset +=4;
                if(hasImage == 1)
                {
                    var length = data.readUInt32BE(offset) + 2;
                    length_bitmap = length;
                    // console.log(length);
                    offset += 4;
                    let temp = data.toString('utf8',offset,data.length);
                    var image = (''+temp).slice(1);
                }
                else
                {
                    var image = null;
                }
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "TextSize": TextSize,
                    "Parent_ID": layoutId,
                    "X": X,
                    "Y": Y,
                    "Height": height,
                    "Width": width,
                    "Image": image,
                };

                UI_List_Buffer.push(UIdata);
                // let tempArr = this.state.UIList;
                // tempArr.push(UIdata);
                // // console.log(Date.now()," : ","UIdata : ",UIdata);
                
                // UI_List_Buffer = tempArr
                // this.setState({
                //     UIList: tempArr
                // });
            }
            else if(widgetType.includes("ImageView"))
            {

                console.log(Date.now()," : ","handleData : creating ImageView data");
                //buffer 합치기
                //bitmap data 받아오기 및 저장
                
                // console.log('Buffer Length : ',data.length);
                // const bufferbuffer = data.concat(bitmap);
                
                var height = data.readUInt32BE(offset)* 0.75;
                // console.log("Height : ",height);
                offset +=4;
                var width = data.readUInt32BE(offset)* 0.75;
                offset +=4;
                // console.log("Width : ",width);

                var length = data.readUInt32BE(offset) + 2;
                length_bitmap = length;
                // console.log(length);
                offset += 4;
                
                
                
                var rest = data.length - offset;
                
                // buffer_cur = 0;
                let temp = data.toString('utf8',offset,data.length);
                let current_bitmap = (''+temp).slice(1);
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Length": length,
                    
                    "Height": height,
                    "Width": width,
                    "Parent_ID": layoutId,
                    "X": X,
                    "Y": Y,
                    "Image": current_bitmap,
                };
                // buffer_cur += rest;
                //console.log('client , ',client);
                // cur_id = id;
                //console.log("UIList ",this.state.UIList[0]);
                UI_List_Buffer.push(UIdata);
                // let tempArr = this.state.UIList;
                // tempArr.push(UIdata);
                // // console.log("UIdata : ",UIdata);
                // //console.log("UIList ",this.state.UIList[0]);
                // this.setState({
                //     UIList: tempArr
                // });
                //Acc_or_Han = 1;
            }
            else if(widgetType.includes("Button"))
            {
                console.log(Date.now()," : ","handleData : creating Button data");
                // console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                // console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                var height = data.readUInt32BE(offset)* 0.728;
                // console.log("Height : ",height);
                offset +=4;
                var width = data.readUInt32BE(offset)* 0.728;
                // console.log("Width : ",width);
                offset += 4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "Height": height,
                    "Parent_ID": layoutId,
                    "Width": width,
                    "X": X,
                    "Y": Y,
                };
                UI_List_Buffer.push(UIdata);
                // let tempArr = this.state.UIList;
                // tempArr.push(UIdata);
                // // console.log(Date.now()," : ","UIdata : ",UIdata);
                // this.setState({
                //     UIList: tempArr
                // });
            }
            else if(widgetType.includes("OtherView"))
            {
                console.log(Date.now()," : ","handleData : creating OtherView data");
                var height = data.readUInt32BE(offset)*0.728;
                offset +=4;
                var width = data.readUInt32BE(offset)*0.728;
                offset +=4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": 'others',
                    "Height": height,
                    "Width": width,
                    "Parent_ID":layoutId,
                    "X": X,
                    "Y": Y,
                };
                UI_List_Buffer.push(UIdata);
                // let tempArr = this.state.UIList;
                // tempArr.push(UIdata);
                // // console.log(Date.now()," : ","UIdata : ",UIdata);
                // this.setState({
                //     UIList: tempArr
                // });
            }
            
            
        }

        else if(isUpdate == 1){//update mode
            let tempArr = this.state.UIList;
            tempArr.forEach(function (targetUI){
                if(id == targetUI.ID){
                    let stringSize = data.readUInt32BE(offset)+2;
                    // console.log(Date.now()," : ","stringSize: ",offset, stringSize);
                    offset += 4;
                    let method = data.toString('utf8',offset,offset+stringSize);
                    offset +=stringSize;
                    let typeFlag = data.readUInt32BE(offset);
                    offset += 4;
                    //get parameters
                    console.log("typeFlag : " + typeFlag);
                    if(typeFlag == 1){
                        var param = data.readFloatBE(offset);
                        offset+=4;
                    }
                    if(typeFlag == 2){
                        var param = data.readUInt32BE(offset);
                        offset +=4;
                    }
                    if(typeFlag == 3){
                        stringSize = data.readUInt32BE(offset)+2;
                        offset +=4;
                        let param_temp = data.toString('utf8',offset,offset+stringSize);
                        var param = (''+param_temp).slice(1);
                        offset +=stringSize;
                        console.log("string : ", param);
                    }

                    //UI update
                    if(method.includes("setTextColor")){
                        // console.log(Date.now()," : ","color : ",param);
                        let bb = param & 0x000000FF;
                        let gg = param & 0x0000FF00;
                        let rr = param & 0x00FF0000;
                        let aa = param & 0xFF000000;
                        rr = rr<<8;
                        bb = bb<<8;
                        gg = gg<<8;
                        aa = aa>>>24;
                        // console.log(Date.now()," : ",rr,bb,gg,aa)
                        var newColor = rr|bb|gg|aa; //android native in aarrggbb react-native in rrggbbaa
                        targetUI.Color = newColor>>>0;
                        // if(param == 4278190335)
                        // {
                        //     targetUI.Color= newColor>>>0;
                        // }
                        // else if(param == 4294901760){
                        //     targetUI.Color = newColor>>>0; //shift operator to make it unsigned
                        // }
                    }
                    // else if(method.includes("setTextSize")){
                    //     targetUI.TextSize = param;
                    // }
                    else if(method.includes("setImage")){
                        targetUI.Image = param;
                    }
                    else if(method.includes("setText")){
                        targetUI.Text = param;
                    }
                }
                console.log(targetUI);
            });
            // console.log(Date.now()," : ","UIList ",this.state.UIList[0]);
            this.setState({
                UIList: tempArr
            });
            // console.log(Date.now()," : ","UIList", this.state.UIList[0]);
        }
        else if(isUpdate ==2) {//distribute layout
            
            var layout_type = data.readUInt32BE(offset);
            offset+=4;
            if(layout_type == 0){
                console.log(Date.now()," : ","handleData : creating LinearLayout data");
                //Linear layout
                var orientation = data.readUInt32BE(offset);
                offset+=4;
                var width = data.readUInt32BE(offset)* 0.728;
                offset +=4;
                var height = data.readUInt32BE(offset)* 0.728;
                var layout_Data = {
                    "ID": id,
                    "Layout_Type": layout_type,
                    "Orientation": orientation,
                    "Height": height,
                    "Width": width,
                }

            }
            else if(layout_type == 1) { //other layout
                console.log(Date.now()," : ","handleData : creating OtherLayout data");
                var width = data.readUInt32BE(offset)* 0.728;
                offset +=4;
                var height = data.readUInt32BE(offset)* 0.728;
                offset +=4;
                var X = data.readFloatBE(offset);
                offset +=4;
                var Y = data.readFloatBE(offset);
                offset +=4;
                var layout_Data = {
                    "ID": id,
                    "Layout_Type": layout_type,
                    "Height": height,
                    "Width": width,
                    "X": X,
                    "Y": Y,
                }
            }
            tempArr = this.state.LayoutList;
            tempArr.push(layout_Data);
            this.setState({
                LayoutList: tempArr
            });
            console.log(Date.now()," : ",layout_Data);
            
        }
        else if(isUpdate == 3)
        {
            console.log(Date.now()," : ", "handleData : endOf Distribution");
            let tempArr = this.state.UIList;
            UI_List_Buffer.forEach( function(item) {
                tempArr.push(item);
            } )
            UI_List_Buffer = new Array();
            this.setState({
                    UIList: tempArr
                });
        }
    
        
        
    };
    onPressInListener = (e) => {
        var target_id = e.target._internalFiberInstanceHandleDEV.memoizedProps.id;
        //console.log(e.target._internalFiberInstanceHandleDEV.memoizedProps);
        let tempArr = this.state.UIList;

            let buffer = Buffer.alloc(1000);
            let offset = 0;
            buffer.writeUInt32BE(target_id,offset); //id
            console.log("send target id : ",target_id);
            offset +=4;
            buffer.writeUInt32BE(1,offset); //typeEvent
            offset +=4;
            buffer.writeUInt32BE(0,offset); //down
            offset +=4;
            buffer.writeFloatBE(e.nativeEvent.locationX/0.728,offset);
            offset +=4;
            buffer.writeFloatBE(e.nativeEvent.locationY/0.728,offset);
            offset +=4;
            client.write(buffer); 

    }
    onPressOutListener = (e,H,W) => {
        var target_id = e.target._internalFiberInstanceHandleDEV.memoizedProps.id;
        let tempArr = this.state.UIList;
        
            let buffer = Buffer.alloc(1000);
            let offset = 0;
            buffer.writeUInt32BE(target_id,offset); //id
            console.log("send target id : ",target_id);
            offset +=4;
            buffer.writeUInt32BE(1,offset); //typeEvent
            offset +=4;
            buffer.writeUInt32BE(1,offset); //down
            offset +=4;
            buffer.writeFloatBE(e.nativeEvent.locationX/0.728,offset);
            offset +=4;
            buffer.writeFloatBE(e.nativeEvent.locationY/0.728,offset);
            offset +=4;
            client.write(buffer);

    }

    moveComponent = (id,e) => {
        let tempArr = this.state.UIList;
        var target_id = id;
        tempArr.forEach(function (targetUI) {
            if(targetUI.ID == target_id){
                targetUI.X = e.nativeEvent.locationX
                targetUI.Y = e.nativeEvent.locationY
            }
        });
        this.setState({
            UIList: tempArr
        });
    }
    rerender = () => {
        let tempArr = this.state.UIList;
        this.setState({
            UIList: tempArr
        });
    }
    TextChangeListener = (e) =>{
        console.log(e.target.id);
        const {name,type,text} = e.nativeEvent;
        var target_id = e.target._internalFiberInstanceHandleDEV.memoizedProps.id;
        let tempArr = this.state.UIList;
        tempArr.forEach(function (targetUI) {
            if(targetUI.ID == target_id){
                console.log(target_id, text);
                targetUI.Text = text;
                let buffer = Buffer.alloc(1000);
                let offset = 0;
                buffer.writeUInt32BE(target_id,offset);
                offset+=4;
                buffer.writeUInt32BE(2,offset); //text event flag
                offset+=4;

                //let stringSize =  buffer.write(''+text+'',offset);
                //offset += stringSize;
                let buffer2 = Buffer.alloc(100);
                let length = buffer2.write(text);
                buffer.writeUInt32BE(length,offset);
                offset+=4;
                client.write(buffer);
                client.write(buffer2);
            }
        });
        this.setState({
            UIList: tempArr
        });
    };
    render() {
        const utf8 = require('utf8');
        let Layout = this.state.LayoutList.map((item,index)=> {
            if(item.Layout_Type == 0) {
                //LinearLayout
                return (
                    <View key={item.ID}>
                        <LinearLayout 
                            UIList={this.state.UIList} 
                            setLinearLayout={item}
                            TextChangeListener={this.TextChangeListener}
                            onPressInListener={this.onPressInListener}
                            onPressOutListener={this.onPressOutListener}/>
                    </View>
                )

            }
            else if(item.Layout_Type == 1) {
                return (
                    <View key={item.ID}>
                        <OtherLayout
                            UIList={this.state.UIList}
                            setOtherLayout={item}
                            TextChangeListener={this.TextChangeListener}
                            moveComponent={this.moveComponent}
                            rerender={this.rerender}
                            onPressInListener={this.onPressInListener}
                            onPressOutListener={this.onPressOutListener}/>
                    </View>
                )
            }
        });

        // let Arr = this.state.UIList.map((item,index)=>{
        //     console.log(item.WidgetType.includes("EditText"));
        //     if(item.WidgetType.includes("EditText")){
        //         console.log("App : pass to EditText");
        //         return (
        //             <View key={item.ID} style={{alignItems:'flex-start', backgroundColor: 'blue', borderBottonWidth : StyleSheet.hairlineWidth}}>
        //                 <TextInput style={{fontSize: item.TextSize,  textAlign: 'left', padding: 2, color: item.Color}} 
        //                     value={item.Text}
        //                     id={item.ID}
        //                     onChange={this.TextChangeListener}>
        //                 </TextInput>
        //             </View>
        //         );
        //     }
        //     if(item.WidgetType.includes("TextView")){
        //         console.log("App : pass to TextView");
        //         return (
        //             <View key={item.ID} style={{alignItems:'flex-start'}}>
        //                 <Text  style={{fontSize: item.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}>
        //                     {item.Text}
        //                 </Text>
        //             </View>
        //         );
        //     }
        //     if(item.WidgetType.includes("Button")){
        //         console.log("App : pass to Button");
        //         return(
        //             <View key={item.ID} style={{height: item.Height, width: item.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black',borderBottomWidth: StyleSheet.hairlineWidth}}>
        //                 <Pressable style={{height: item.Height, width: item.Width, alignContent: 'center',   justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
        //                     id={item.ID}
        //                     onPressIn={this.onPressInListener}
        //                     onPressOut={this.onPressOutListener}>
        //                     <Text 
        //                         style={{fontSize: 30, textAlign: 'center', alignContent: 'center', color: 'black'}}
        //                         id={item.ID}> 
        //                             {item.Text} 
        //                     </Text>
                            
        //                 </Pressable>
        //             </View>
        //         )
        //     }
            
        //     return(
        //         <Text> hi </Text>
        //     );
            
            
        // });
        return (
            <SafeAreaView Style={styles.container}>
                {Layout}
            </SafeAreaView>
        );
    };
}

const styles = StyleSheet.create({
    container: {
        alignItems: "center",
    },
});
export default App;

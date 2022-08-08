import React, {useState, Component} from 'react';
import {StyleSheet, View, Text,TextInput, Button, ScrollView, SafeAreaView, Pressable} from 'react-native';
import TcpSocket from "react-native-tcp-socket";
import utf8 from 'utf8';
import {Buffer} from 'buffer';

var client;

class App extends Component {
    constructor(props){
        super(props);
        this._Connect_to_Server();
        
    };
    state = {
        UIList: new Array(),
    };
    _Connect_to_Server = (bufer) => {
        client = TcpSocket.createConnection({host: "192.168.0.23", port: 5673}, ()=>{
            console.log("connection established");
        });
        client.on('data', (data) => this.handleData(data));
        
    };
    
    handleData(data) {
        var data = data;
        var offset = 0;
        console.log('message received',data);
        console.log('id : ',offset, data.readUInt32BE(offset));
        var id = data.readUInt32BE(offset);
        offset += 4;
        console.log('isUpdate : ',offset,data.readIntBE(offset,1));
        var isUpdate = data.readIntBE(offset,1);
        offset +=1;
        if(isUpdate == 0) //distribute mode
        {
            //read WidgetType
            
            console.log('String Length',offset,data.readUInt32BE(offset));
            var stringSize = data.readUInt32BE(offset)+2;
            offset += 4;
            console.log('widgetType : ',offset,data.toString('utf8',offset,offset+stringSize));
            var widgetType = data.toString('utf8',offset,offset+stringSize);
            offset += stringSize;
            console.log("check : ",widgetType,widgetType.includes("EditText"));
            if(widgetType.includes("EditText")==1)
            {
                console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                console.log('TextSize : ',data.readFloatBE(offset));
                var TextSize = data.readFloatBE(offset);
                offset += 4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "TextSize": TextSize,
                    "isUpdate":isUpdate,
                    "Color": 'black',
                };
                console.log("UIList ",this.state.UIList[0]);
                let tempArr = this.state.UIList;
                tempArr.push(UIdata);
                console.log("UIList ",this.state.UIList[0]);
                this.setState({
                    UIList: tempArr
                });
                console.log("UIList ",this.state.UIList[0]);
            }
            else if (widgetType.includes("TextView"))
            {
                
                console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                console.log('TextSize : ',data.readFloatBE(offset));
                var TextSize = data.readFloatBE(offset);
                offset += 4
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "TextSize": TextSize,
                    "isUpdate":isUpdate,
                };
                console.log("UIList ",this.state.UIList[0]);
                let tempArr = this.state.UIList;
                tempArr.push(UIdata);
                console.log("UIList ",this.state.UIList[0]);
                this.setState({
                    UIList: tempArr
                });
            }
            else if(widgetType.includes("Button"))
            {
                console.log('String Length',offset,data.readUInt32BE(offset));
                stringSize = data.readUInt32BE(offset)+2;
                offset += 4;
                console.log('Text : ',data.toString('utf8',offset,offset+stringSize));
                var temp_text = data.toString('utf8',offset,offset+stringSize);
                var text = (''+temp_text).slice(1);
                offset += stringSize;
                var height = data.readUInt32BE(offset)* 0.75;
                console.log("Height : ",height);
                offset +=4;
                var width = data.readUInt32BE(offset)* 0.75;
                console.log("Width : ",width);
                offset += 4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": text,
                    "Height": height,
                    "isUpdate":isUpdate,
                    "Width": width,
                };
                console.log("UIList ",this.state.UIList[0]);
                let tempArr = this.state.UIList;
                tempArr.push(UIdata);
                console.log("UIList ",this.state.UIList[0]);
                this.setState({
                    UIList: tempArr
                });
            }
            
            
        }

        else if(isUpdate == 1){//update mode
            let tempArr = this.state.UIList;
            tempArr.forEach(function (targetUI){
                if(id == targetUI.ID){
                    let stringSize = data.readUInt32BE(offset)+2;
                    console.log("stringSize: ",offset, stringSize);
                    offset += 4;
                    let method = data.toString('utf8',offset,offset+stringSize);
                    offset +=stringSize;
                    let typeFlag = data.readUInt32BE(offset);
                    offset += 4;
                    //get parameters
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
                    }

                    //UI update
                    if(method.includes("setTextColor")){
                        console.log("color : ",param);
                        let bb = param & 0x000000FF;
                        let gg = param & 0x0000FF00;
                        let rr = param & 0x00FF0000;
                        let aa = param & 0xFF000000;
                        rr = rr<<8;
                        bb = bb<<8;
                        gg = gg<<8;
                        aa = aa>>>24;
                        console.log(rr,bb,gg,aa)
                        var newColor = rr|bb|gg|aa; //android native in aarrggbb react-native in rrggbbaa
                        if(param == 4278190335)
                        {
                            targetUI.Color= newColor>>>0;
                        }
                        else if(param == 4294901760){
                            targetUI.Color = newColor>>>0; //shift operator to make it unsigned
                        }
                    }
                    else if(method.includes("setText")){
                        targetUI.Text = param;
                    }
                }
            });
            console.log("UIList ",this.state.UIList[0]);
            this.setState({
                UIList: tempArr
            });
            console.log("UIList", this.state.UIList[0]);
        }
        
    };
    onPressInListener = (e) => {
        var target_id = e.target._internalFiberInstanceHandleDEV.memoizedProps.id;
        console.log(e.target._internalFiberInstanceHandleDEV.memoizedProps);
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
            client.write(buffer); 

    }
    onPressOutListener = (e) => {
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
            client.write(buffer);

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
        let Arr = this.state.UIList.map((item,index)=>{
            console.log(item.WidgetType.includes("EditText"));
            if(item.WidgetType.includes("EditText")){
                console.log("EditText");
                return (
                    <View key={item.ID} style={{alignItems:'flex-start'}}>
                        <TextInput style={{fontSize: item.TextSize,  textAlign: 'left', padding: 2, color: item.Color}} 
                            value={item.Text}
                            id={item.ID}
                            onChange={this.TextChangeListener}>
                        </TextInput>
                    </View>
                );
            }
            if(item.WidgetType.includes("TextView")){
                console.log("TextView");
                return (
                    <View key={item.ID} style={{alignItems:'flex-start'}}>
                        <Text  style={{fontSize: item.TextSize, textAlign: 'left', fontWeight: '500', width: 350}}>
                            {item.Text}
                        </Text>
                    </View>
                );
            }
            if(item.WidgetType.includes("Button")){
                console.log("Button");
                return(
                    <View key={item.ID} style={{height: item.Height, width: item.Width, alignContent: 'center', alignItems: "center",backgroundColor: 'black'}}>
                        <Pressable style={{height: item.Height, width: item.Width, alignContent: 'center',  borderBottomWidth: StyleSheet.hairlineWidth, justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}
                            id={item.ID}
                            onPressIn={this.onPressInListener}
                            onPressOut={this.onPressOutListener}>
                            <Text 
                                style={{fontSize: 30, textAlign: 'center', alignContent: 'center', color: 'black'}}
                                id={item.ID}> 
                                    {item.Text} 
                            </Text>
                            
                        </Pressable>
                    </View>
                )
            }
            
            return(
                <Text> hi </Text>
            );
            
            
        });
        return (
            <SafeAreaView Style={styles.container}>
                {Arr}
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
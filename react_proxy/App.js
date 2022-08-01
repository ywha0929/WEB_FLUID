import React, {useState, Component} from 'react';
import {StyleSheet, View, Text,TextInput, Button, ScrollView, SafeAreaView, Pressable} from 'react-native';
import TcpSocket from "react-native-tcp-socket";
import UIs from './components/UI';
import utf8 from 'utf8';



class App extends Component {
    constructor(props){
        super(props);
        this._Connect_to_Server();
        
    };
    state = {
        UIList: new Array()
    };
    _Connect_to_Server = () => {
        const client = TcpSocket.createConnection({host: "192.168.0.23", port: 5673}, ()=>{
            console.log("connection established");
        });
        client.on('data', (data) => this.handleData(data));
    };
    makeEditText = (id,text,textSize) =>
    {   
        setEditTextData([
            ...EditTextData,
            {valueID: id, valueText: text, valueTextSize: textSize},
        ]);
        
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
        if(isUpdate == 0) //distribute mode
        {
            //read WidgetType
            offset +=1;
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
        
    };
    render() {
        const utf8 = require('utf8');
        let Arr = this.state.UIList.map((item,index)=>{
            console.log(item.WidgetType.includes("EditText"));
            if(item.WidgetType.includes("EditText")){
                console.log("EditText");
                return (
                    <View key={item.ID} style={{alignItems:'flex-start'}}>
                        <TextInput style={{fontSize: item.TextSize,  textAlign: 'left', padding: 2}} value={item.Text} >
                            
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
                        <Pressable style={{height: item.Height, width: item.Width, alignContent: 'center',  borderBottomWidth: StyleSheet.hairlineWidth, justifyContent: 'center', alignItems: "center", backgroundColor: 'skyblue'}}>
                            <Text style={{fontSize: 30, textAlign: 'center', alignContent: 'center', color: 'black'}}> {item.Text} </Text>
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
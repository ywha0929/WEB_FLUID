import React, {useState, Component} from 'react';
import {StyleSheet, View, Text,TextInput, Button} from 'react-native';
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
                var Text = data.toString('utf8',offset,offset+stringSize);
                offset += stringSize;
                console.log('TextSize : ',data.readFloatBE(offset));
                var TextSize = data.readFloatBE(offset);
                offset += 4
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": Text,
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
                var Text = data.toString('utf8',offset,offset+stringSize);
                offset += stringSize;
                var height = data.readUInt32BE(offset);
                console.log("Height : ",height);
                offset +=4;
                var width = data.readUInt32BE(offset);
                console.log("Width : ",width);
                offset += 4;
                let UIdata = {
                    "WidgetType": widgetType,
                    "ID": id,
                    "Text": Text,
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
                console.log(item.Text.length);
                return (
                    <View key={item.ID} style={{flex:1}}>
                        <TextInput  style={{fontSize: item.TextSize, value: item.Text}}>
                            
                        </TextInput>
                    </View>
                );
            }
            if(item.WidgetType.includes("TextView")){
                console.log(item.Text);
                return (
                    <View key={item.ID} style={{flex: 1}}>
                        <Text  style={{fontSize: item.TextSize}}>
                            {`hi ${item.Text}`}
                        </Text>
                    </View>
                );
            }
            if(item.WidgetType.includes("Button")){
                console.log("type: ",typeof (item.Text));
                return(
                    <View key={item.ID} style={{height:item.Height, width: item.Width,flexDirection: 'column'}}>
                        <Button  style={{flex: 1}} title={(item.Text)}>
                        </Button>
                    </View>
                )
            }
            
            return(
                <Text> hi </Text>
            );
            
            
        });
        return (
            <View style={styles.container}>
                {Arr}
            </View>
        );
    };
}

const styles = StyleSheet.create({
    container: {
        alignItems: "center",
        flexDirection: 'column',
    },
});
export default App;
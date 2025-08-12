package com.voting.spring_boot_project.contract;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class Voting extends Contract {
    public static final String BINARY = "60806040526001600355348015601457600080fd5b5033600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506128fe806100656000396000f3fe608060405234801561001057600080fd5b506004361061010b5760003560e01c80638a241f4c116100a25780639fdb1457116100715780639fdb1457146102e2578063aba21009146102fe578063b023a1ca1461032e578063d3c9a7411461034a578063dc6bfbb5146103665761010b565b80638a241f4c146102365780638f47b1461461026657806394c790bb14610296578063995e4339146102b25761010b565b806355b7c597116100de57806355b7c597146101805780635c632b38146101b15780637465e7d4146101e657806377955245146102185761010b565b80632ad1f1db146101105780632ec7d5ae1461012c5780634eecbdee1461014857806350d9158314610164575b600080fd5b61012a60048036038101906101259190611ce3565b610384565b005b61014660048036038101906101419190611d23565b610491565b005b610162600480360381019061015d9190611f07565b610852565b005b61017e60048036038101906101799190611f8a565b610d58565b005b61019a60048036038101906101959190611ce3565b6110e5565b6040516101a8929190611fec565b60405180910390f35b6101cb60048036038101906101c69190611d23565b611116565b6040516101dd9695949392919061203f565b60405180910390f35b61020060048036038101906101fb91906120a0565b61117f565b60405161020f939291906120e0565b60405180910390f35b6102206111d0565b60405161022d9190612117565b60405180910390f35b610250600480360381019061024b9190611d23565b6111f6565b60405161025d91906121f0565b60405180910390f35b610280600480360381019061027b91906120a0565b61139d565b60405161028d9190612263565b60405180910390f35b6102b060048036038101906102ab91906120a0565b611448565b005b6102cc60048036038101906102c79190611d23565b611647565b6040516102d991906121f0565b60405180910390f35b6102fc60048036038101906102f791906120a0565b611788565b005b61031860048036038101906103139190611ce3565b611987565b60405161032591906122ad565b60405180910390f35b61034860048036038101906103439190611ce3565b6119da565b005b610364600480360381019061035f9190611d23565b611b29565b005b61036e611bea565b60405161037b91906122c8565b60405180910390f35b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610414576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161040b90612366565b60405180910390fd5b816000806000838152602001908152602001600020905080600101544210610471576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610468906123d2565b60405180910390fd5b826000808681526020019081526020016000206002018190555050505050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610521576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161051890612366565b60405180910390fd5b80600080600083815260200190815260200160002090508060020154816001015461054c9190612421565b42101561058e576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610585906124a1565b60405180910390fd5b8260008082815260200190815260200160002060030160009054906101000a900460ff16156105f2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016105e99061250d565b60405180910390fd5b60008080600087815260200190815260200160002060060154905060005b8181101561067f5782600260008981526020019081526020016000206000838152602001908152602001600020600101541115610672576002600088815260200190815260200160002060008281526020019081526020016000206001015492505b8080600101915050610610565b5060008167ffffffffffffffff81111561069c5761069b611d66565b5b6040519080825280602002602001820160405280156106ca5781602001602082028036833780820191505090505b5090506000805b838110156107435784600260008b815260200190815260200160002060008381526020019081526020016000206001015403610736578083838151811061071b5761071a61252d565b5b60200260200101818152505081806107329061255c565b9250505b80806001019150506106d1565b5060008167ffffffffffffffff8111156107605761075f611d66565b5b60405190808252806020026020018201604052801561078e5781602001602082028036833780820191505090505b50905060005b828110156107e3578381815181106107af576107ae61252d565b5b60200260200101518282815181106107ca576107c961252d565b5b6020026020010181815250508080600101915050610794565b50806000808b8152602001908152602001600020600401908051906020019061080d929190611bf0565b507fc9e72c5cb44207ce2335ae5d4c335ffa4f1689a5cedfce37f4b3e579bc5737c6898260405161083f9291906125a4565b60405180910390a1505050505050505050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146108e2576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108d990612366565b60405180910390fd5b428411610924576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161091b90612620565b60405180910390fd5b60018211610967576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161095e9061268c565b60405180910390fd5b60008151116109ab576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016109a2906126f8565b60405180910390fd5b6000600360008154809291906109c09061255c565b9190505590506000806000838152602001908152602001600020905081816000018190555085816001018190555084816002018190555060008160030160006101000a81548160ff0219169083151502179055508367ffffffffffffffff811115610a2e57610a2d611d66565b5b604051908082528060200260200182016040528015610a5c5781602001602082028036833780820191505090505b50816004019080519060200190610a74929190611bf0565b50338160050160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555083816006018190555060005b84811015610b63576040518060400160405280828152602001600081525060026000858152602001908152602001600020600083815260200190815260200160002060008201518160000155602082015181600101559050507ffcf3b1aa65a464cef2889608f99e8b8c0f680a4be6c2acb9d961c536a5a9294b8382604051610b4e929190611fec565b60405180910390a18080600101915050610ac4565b5060005b8351811015610d18576000848281518110610b8557610b8461252d565b5b60200260200101519050600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1603610bfe576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610bf590612764565b60405180910390fd5b6001600085815260200190815260200160002060008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160009054906101000a900460ff1615610c9f576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610c96906127d0565b60405180910390fd5b600180600086815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160006101000a81548160ff021916908315150217905550508080600101915050610b67565b507f8240970fe48e7326c1988bac710fa94e48ffac4c80da6f31d40e52fc71df105982604051610d4891906122c8565b60405180910390a1505050505050565b8260008082815260200190815260200160002060030160009054906101000a900460ff1615610dbc576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610db39061250d565b60405180910390fd5b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610e4c576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610e4390612366565b60405180910390fd5b6001600085815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160009054906101000a900460ff16610eec576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610ee39061283c565b60405180910390fd5b6001600085815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160019054906101000a900460ff1615610f8d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610f84906128a8565b60405180910390fd5b600180600086815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160016101000a81548160ff021916908315150217905550826001600086815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010181905550600160026000868152602001908152602001600020600085815260200190815260200160002060010160008282546110889190612421565b925050819055508173ffffffffffffffffffffffffffffffffffffffff167f30befe21dd895620c3a73403f6e63aea5c3c6729202dbdc8eef2e8f8bd78de8085856040516110d7929190611fec565b60405180910390a250505050565b6002602052816000526040600020602052806000526040600020600091509150508060000154908060010154905082565b60006020528060005260406000206000915090508060000154908060010154908060020154908060030160009054906101000a900460ff16908060050160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060060154905086565b6001602052816000526040600020602052806000526040600020600091509150508060000160009054906101000a900460ff16908060000160019054906101000a900460ff16908060010154905083565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60608160008060008381526020019081526020016000209050806002015481600101546112239190612421565b421015611265576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161125c906124a1565b60405180910390fd5b8360008082815260200190815260200160002060030160009054906101000a900460ff16156112c9576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016112c09061250d565b60405180910390fd5b600080600087815260200190815260200160002060060154905060008167ffffffffffffffff8111156112ff576112fe611d66565b5b60405190808252806020026020018201604052801561132d5781602001602082028036833780820191505090505b50905060005b8281101561138f57600260008981526020019081526020016000206000828152602001908152602001600020600101548282815181106113765761137561252d565b5b6020026020010181815250508080600101915050611333565b508095505050505050919050565b6113a5611c3d565b6001600084815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206040518060600160405290816000820160009054906101000a900460ff161515151581526020016000820160019054906101000a900460ff16151515158152602001600182015481525050905092915050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146114d8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016114cf90612366565b60405180910390fd5b816000806000838152602001908152602001600020905080600101544210611535576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161152c906123d2565b60405180910390fd5b6001600085815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160009054906101000a900460ff16156115d6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016115cd906127d0565b60405180910390fd5b600180600086815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160006101000a81548160ff02191690831515021790555050505050565b60608160008060008381526020019081526020016000209050806002015481600101546116749190612421565b4210156116b6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016116ad906124a1565b60405180910390fd5b8360008082815260200190815260200160002060030160009054906101000a900460ff161561171a576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016117119061250d565b60405180910390fd5b60008086815260200190815260200160002060040180548060200260200160405190810160405280929190818152602001828054801561177957602002820191906000526020600020905b815481526020019060010190808311611765575b50505050509350505050919050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611818576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161180f90612366565b60405180910390fd5b816000806000838152602001908152602001600020905080600101544210611875576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161186c906123d2565b60405180910390fd5b6001600085815260200190815260200160002060008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160009054906101000a900460ff16611915576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161190c9061283c565b60405180910390fd5b60006001600086815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000160006101000a81548160ff02191690831515021790555050505050565b61198f611c62565b60026000848152602001908152602001600020600083815260200190815260200160002060405180604001604052908160008201548152602001600182015481525050905092915050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611a6a576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611a6190612366565b60405180910390fd5b816000806000838152602001908152602001600020905080600101544210611ac7576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611abe906123d2565b60405180910390fd5b428311611b09576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611b0090612620565b60405180910390fd5b826000808681526020019081526020016000206001018190555050505050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614611bb9576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401611bb090612366565b60405180910390fd5b600160008083815260200190815260200160002060030160006101000a81548160ff02191690831515021790555050565b60035481565b828054828255906000526020600020908101928215611c2c579160200282015b82811115611c2b578251825591602001919060010190611c10565b5b509050611c399190611c7c565b5090565b6040518060600160405280600015158152602001600015158152602001600081525090565b604051806040016040528060008152602001600081525090565b5b80821115611c95576000816000905550600101611c7d565b5090565b6000604051905090565b600080fd5b600080fd5b6000819050919050565b611cc081611cad565b8114611ccb57600080fd5b50565b600081359050611cdd81611cb7565b92915050565b60008060408385031215611cfa57611cf9611ca3565b5b6000611d0885828601611cce565b9250506020611d1985828601611cce565b9150509250929050565b600060208284031215611d3957611d38611ca3565b5b6000611d4784828501611cce565b91505092915050565b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b611d9e82611d55565b810181811067ffffffffffffffff82111715611dbd57611dbc611d66565b5b80604052505050565b6000611dd0611c99565b9050611ddc8282611d95565b919050565b600067ffffffffffffffff821115611dfc57611dfb611d66565b5b602082029050602081019050919050565b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000611e3d82611e12565b9050919050565b611e4d81611e32565b8114611e5857600080fd5b50565b600081359050611e6a81611e44565b92915050565b6000611e83611e7e84611de1565b611dc6565b90508083825260208201905060208402830185811115611ea657611ea5611e0d565b5b835b81811015611ecf5780611ebb8882611e5b565b845260208401935050602081019050611ea8565b5050509392505050565b600082601f830112611eee57611eed611d50565b5b8135611efe848260208601611e70565b91505092915050565b60008060008060808587031215611f2157611f20611ca3565b5b6000611f2f87828801611cce565b9450506020611f4087828801611cce565b9350506040611f5187828801611cce565b925050606085013567ffffffffffffffff811115611f7257611f71611ca8565b5b611f7e87828801611ed9565b91505092959194509250565b600080600060608486031215611fa357611fa2611ca3565b5b6000611fb186828701611cce565b9350506020611fc286828701611cce565b9250506040611fd386828701611e5b565b9150509250925092565b611fe681611cad565b82525050565b60006040820190506120016000830185611fdd565b61200e6020830184611fdd565b9392505050565b60008115159050919050565b61202a81612015565b82525050565b61203981611e32565b82525050565b600060c0820190506120546000830189611fdd565b6120616020830188611fdd565b61206e6040830187611fdd565b61207b6060830186612021565b6120886080830185612030565b61209560a0830184611fdd565b979650505050505050565b600080604083850312156120b7576120b6611ca3565b5b60006120c585828601611cce565b92505060206120d685828601611e5b565b9150509250929050565b60006060820190506120f56000830186612021565b6121026020830185612021565b61210f6040830184611fdd565b949350505050565b600060208201905061212c6000830184612030565b92915050565b600081519050919050565b600082825260208201905092915050565b6000819050602082019050919050565b61216781611cad565b82525050565b6000612179838361215e565b60208301905092915050565b6000602082019050919050565b600061219d82612132565b6121a7818561213d565b93506121b28361214e565b8060005b838110156121e35781516121ca888261216d565b97506121d583612185565b9250506001810190506121b6565b5085935050505092915050565b6000602082019050818103600083015261220a8184612192565b905092915050565b61221b81612015565b82525050565b6060820160008201516122376000850182612212565b50602082015161224a6020850182612212565b50604082015161225d604085018261215e565b50505050565b60006060820190506122786000830184612221565b92915050565b604082016000820151612294600085018261215e565b5060208201516122a7602085018261215e565b50505050565b60006040820190506122c2600083018461227e565b92915050565b60006020820190506122dd6000830184611fdd565b92915050565b600082825260208201905092915050565b7f4f6e6c7920636f6e74726163742061646d696e2063616e20706572666f726d2060008201527f7468697320616374696f6e000000000000000000000000000000000000000000602082015250565b6000612350602b836122e3565b915061235b826122f4565b604082019050919050565b6000602082019050818103600083015261237f81612343565b9050919050565b7f566f74696e672068617320616c72656164792073746172746564000000000000600082015250565b60006123bc601a836122e3565b91506123c782612386565b602082019050919050565b600060208201905081810360008301526123eb816123af565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b600061242c82611cad565b915061243783611cad565b925082820190508082111561244f5761244e6123f2565b5b92915050565b7f566f74696e6720706572696f6420686173206e6f7420656e6465642079657400600082015250565b600061248b601f836122e3565b915061249682612455565b602082019050919050565b600060208201905081810360008301526124ba8161247e565b9050919050565b7f566f74696e67206973207465726d696e61746564000000000000000000000000600082015250565b60006124f76014836122e3565b9150612502826124c1565b602082019050919050565b60006020820190508181036000830152612526816124ea565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b600061256782611cad565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203612599576125986123f2565b5b600182019050919050565b60006040820190506125b96000830185611fdd565b81810360208301526125cb8184612192565b90509392505050565b7f53746172742074696d65206d75737420626520696e2074686520667574757265600082015250565b600061260a6020836122e3565b9150612615826125d4565b602082019050919050565b60006020820190508181036000830152612639816125fd565b9050919050565b7f4d7573742068617665206174206c656173742074776f2070726f706f73616c73600082015250565b60006126766020836122e3565b915061268182612640565b602082019050919050565b600060208201905081810360008301526126a581612669565b9050919050565b7f4d7573742068617665206174206c65617374206f6e6520766f74657200000000600082015250565b60006126e2601c836122e3565b91506126ed826126ac565b602082019050919050565b60006020820190508181036000830152612711816126d5565b9050919050565b7f496e76616c696420766f74657220616464726573730000000000000000000000600082015250565b600061274e6015836122e3565b915061275982612718565b602082019050919050565b6000602082019050818103600083015261277d81612741565b9050919050565b7f566f74657220697320616c726561647920726567697374657265640000000000600082015250565b60006127ba601b836122e3565b91506127c582612784565b602082019050919050565b600060208201905081810360008301526127e9816127ad565b9050919050565b7f566f746572206973206e6f742072656769737465726564000000000000000000600082015250565b60006128266017836122e3565b9150612831826127f0565b602082019050919050565b6000602082019050818103600083015261285581612819565b9050919050565b7f566f7465722068617320616c726561647920766f746564000000000000000000600082015250565b60006128926017836122e3565b915061289d8261285c565b602082019050919050565b600060208201905081810360008301526128c181612885565b905091905056fea2646970667358221220c69817483e60d7d2bf4792bb8eb8f2d6e94ca22cb2dba702fd8a96ad7f17890164736f6c634300081c0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_BALLOTS = "ballots";

    public static final String FUNC_CONTRACTADMIN = "contractAdmin";

    public static final String FUNC_CREATEBALLOT = "createBallot";

    public static final String FUNC_FINALIZERESULT = "finalizeResult";

    public static final String FUNC_GETPROPOSAL = "getProposal";

    public static final String FUNC_GETRESULT = "getResult";

    public static final String FUNC_GETVOTECOUNTS = "getVoteCounts";

    public static final String FUNC_GETVOTER = "getVoter";

    public static final String FUNC_NEXTBALLOTID = "nextBallotId";

    public static final String FUNC_PROPOSALSBYBALLOT = "proposalsByBallot";

    public static final String FUNC_REGISTERVOTER = "registerVoter";

    public static final String FUNC_TERMINATEVOTING = "terminateVoting";

    public static final String FUNC_UNREGISTERVOTER = "unregisterVoter";

    public static final String FUNC_UPDATEDURATION = "updateDuration";

    public static final String FUNC_UPDATESTARTTIME = "updateStartTime";

    public static final String FUNC_VOTE = "vote";

    public static final String FUNC_VOTERSBYBALLOT = "votersByBallot";

    public static final Event BALLOTCREATED_EVENT = new Event("BallotCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event BALLOTRESULTFINALIZED_EVENT = new Event("BallotResultFinalized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
    ;

    public static final Event PROPOSALCREATED_EVENT = new Event("ProposalCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event VOTERECORDED_EVENT = new Event("VoteRecorded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Voting(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Voting(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Voting(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Voting(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<BallotCreatedEventResponse> getBallotCreatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BALLOTCREATED_EVENT, transactionReceipt);
        ArrayList<BallotCreatedEventResponse> responses = new ArrayList<BallotCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BallotCreatedEventResponse typedResponse = new BallotCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BallotCreatedEventResponse getBallotCreatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BALLOTCREATED_EVENT, log);
        BallotCreatedEventResponse typedResponse = new BallotCreatedEventResponse();
        typedResponse.log = log;
        typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BallotCreatedEventResponse> ballotCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBallotCreatedEventFromLog(log));
    }

    public Flowable<BallotCreatedEventResponse> ballotCreatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BALLOTCREATED_EVENT));
        return ballotCreatedEventFlowable(filter);
    }

    public static List<BallotResultFinalizedEventResponse> getBallotResultFinalizedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BALLOTRESULTFINALIZED_EVENT, transactionReceipt);
        ArrayList<BallotResultFinalizedEventResponse> responses = new ArrayList<BallotResultFinalizedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BallotResultFinalizedEventResponse typedResponse = new BallotResultFinalizedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.resultProposalIds = (List<BigInteger>) ((Array) eventValues.getNonIndexedValues().get(1)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BallotResultFinalizedEventResponse getBallotResultFinalizedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BALLOTRESULTFINALIZED_EVENT, log);
        BallotResultFinalizedEventResponse typedResponse = new BallotResultFinalizedEventResponse();
        typedResponse.log = log;
        typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.resultProposalIds = (List<BigInteger>) ((Array) eventValues.getNonIndexedValues().get(1)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<BallotResultFinalizedEventResponse> ballotResultFinalizedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBallotResultFinalizedEventFromLog(log));
    }

    public Flowable<BallotResultFinalizedEventResponse> ballotResultFinalizedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BALLOTRESULTFINALIZED_EVENT));
        return ballotResultFinalizedEventFlowable(filter);
    }

    public static List<ProposalCreatedEventResponse> getProposalCreatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPOSALCREATED_EVENT, transactionReceipt);
        ArrayList<ProposalCreatedEventResponse> responses = new ArrayList<ProposalCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProposalCreatedEventResponse typedResponse = new ProposalCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.proposalId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ProposalCreatedEventResponse getProposalCreatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPOSALCREATED_EVENT, log);
        ProposalCreatedEventResponse typedResponse = new ProposalCreatedEventResponse();
        typedResponse.log = log;
        typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.proposalId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<ProposalCreatedEventResponse> proposalCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getProposalCreatedEventFromLog(log));
    }

    public Flowable<ProposalCreatedEventResponse> proposalCreatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPOSALCREATED_EVENT));
        return proposalCreatedEventFlowable(filter);
    }

    public static List<VoteRecordedEventResponse> getVoteRecordedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(VOTERECORDED_EVENT, transactionReceipt);
        ArrayList<VoteRecordedEventResponse> responses = new ArrayList<VoteRecordedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VoteRecordedEventResponse typedResponse = new VoteRecordedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.voter = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.proposalId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static VoteRecordedEventResponse getVoteRecordedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(VOTERECORDED_EVENT, log);
        VoteRecordedEventResponse typedResponse = new VoteRecordedEventResponse();
        typedResponse.log = log;
        typedResponse.voter = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.ballotId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.proposalId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<VoteRecordedEventResponse> voteRecordedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getVoteRecordedEventFromLog(log));
    }

    public Flowable<VoteRecordedEventResponse> voteRecordedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VOTERECORDED_EVENT));
        return voteRecordedEventFlowable(filter);
    }

    public RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, String, BigInteger>> ballots(
            BigInteger param0) {
        final Function function = new Function(FUNC_BALLOTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, String, BigInteger>>(function,
                new Callable<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, String, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, BigInteger, Boolean, String, BigInteger> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, BigInteger, Boolean, String, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> contractAdmin() {
        final Function function = new Function(FUNC_CONTRACTADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> createBallot(BigInteger _startTime,
            BigInteger _duration, BigInteger _proposalCount, List<String> _voters) {
        final Function function = new Function(
                FUNC_CREATEBALLOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_startTime), 
                new org.web3j.abi.datatypes.generated.Uint256(_duration), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposalCount), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_voters, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> finalizeResult(BigInteger _ballotId) {
        final Function function = new Function(
                FUNC_FINALIZERESULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Proposal> getProposal(BigInteger _ballotId, BigInteger _proposalId) {
        final Function function = new Function(FUNC_GETPROPOSAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposalId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Proposal>() {}));
        return executeRemoteCallSingleValueReturn(function, Proposal.class);
    }

    public RemoteFunctionCall<List> getResult(BigInteger _ballotId) {
        final Function function = new Function(FUNC_GETRESULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getVoteCounts(BigInteger _ballotId) {
        final Function function = new Function(FUNC_GETVOTECOUNTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Voter> getVoter(BigInteger _ballotId, String _voterAddress) {
        final Function function = new Function(FUNC_GETVOTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.Address(160, _voterAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Voter>() {}));
        return executeRemoteCallSingleValueReturn(function, Voter.class);
    }

    public RemoteFunctionCall<BigInteger> nextBallotId() {
        final Function function = new Function(FUNC_NEXTBALLOTID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> proposalsByBallot(BigInteger param0,
            BigInteger param1) {
        final Function function = new Function(FUNC_PROPOSALSBYBALLOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0), 
                new org.web3j.abi.datatypes.generated.Uint256(param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> registerVoter(BigInteger _ballotId,
            String voter) {
        final Function function = new Function(
                FUNC_REGISTERVOTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.Address(160, voter)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> terminateVoting(BigInteger _ballotId) {
        final Function function = new Function(
                FUNC_TERMINATEVOTING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unregisterVoter(BigInteger _ballotId,
            String voter) {
        final Function function = new Function(
                FUNC_UNREGISTERVOTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.Address(160, voter)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateDuration(BigInteger _ballotId,
            BigInteger _newDuration) {
        final Function function = new Function(
                FUNC_UPDATEDURATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.generated.Uint256(_newDuration)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateStartTime(BigInteger _ballotId,
            BigInteger _newStartTime) {
        final Function function = new Function(
                FUNC_UPDATESTARTTIME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.generated.Uint256(_newStartTime)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> vote(BigInteger _ballotId, BigInteger _proposalId,
            String _voterAddress) {
        final Function function = new Function(
                FUNC_VOTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_ballotId), 
                new org.web3j.abi.datatypes.generated.Uint256(_proposalId), 
                new org.web3j.abi.datatypes.Address(160, _voterAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple3<Boolean, Boolean, BigInteger>> votersByBallot(
            BigInteger param0, String param1) {
        final Function function = new Function(FUNC_VOTERSBYBALLOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0), 
                new org.web3j.abi.datatypes.Address(160, param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<Boolean, Boolean, BigInteger>>(function,
                new Callable<Tuple3<Boolean, Boolean, BigInteger>>() {
                    @Override
                    public Tuple3<Boolean, Boolean, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<Boolean, Boolean, BigInteger>(
                                (Boolean) results.get(0).getValue(), 
                                (Boolean) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    @Deprecated
    public static Voting load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Voting(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Voting load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Voting(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Voting load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Voting(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Voting load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Voting(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Voting> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Voting.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    public static RemoteCall<Voting> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Voting.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Voting> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Voting.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<Voting> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Voting.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class Proposal extends StaticStruct {
        public BigInteger index;

        public BigInteger voteCount;

        public Proposal(BigInteger index, BigInteger voteCount) {
            super(new org.web3j.abi.datatypes.generated.Uint256(index), 
                    new org.web3j.abi.datatypes.generated.Uint256(voteCount));
            this.index = index;
            this.voteCount = voteCount;
        }

        public Proposal(Uint256 index, Uint256 voteCount) {
            super(index, voteCount);
            this.index = index.getValue();
            this.voteCount = voteCount.getValue();
        }
    }

    public static class Voter extends StaticStruct {
        public Boolean isRegistered;

        public Boolean hasVoted;

        public BigInteger vote;

        public Voter(Boolean isRegistered, Boolean hasVoted, BigInteger vote) {
            super(new org.web3j.abi.datatypes.Bool(isRegistered), 
                    new org.web3j.abi.datatypes.Bool(hasVoted), 
                    new org.web3j.abi.datatypes.generated.Uint256(vote));
            this.isRegistered = isRegistered;
            this.hasVoted = hasVoted;
            this.vote = vote;
        }

        public Voter(Bool isRegistered, Bool hasVoted, Uint256 vote) {
            super(isRegistered, hasVoted, vote);
            this.isRegistered = isRegistered.getValue();
            this.hasVoted = hasVoted.getValue();
            this.vote = vote.getValue();
        }
    }

    public static class BallotCreatedEventResponse extends BaseEventResponse {
        public BigInteger ballotId;
    }

    public static class BallotResultFinalizedEventResponse extends BaseEventResponse {
        public BigInteger ballotId;

        public List<BigInteger> resultProposalIds;
    }

    public static class ProposalCreatedEventResponse extends BaseEventResponse {
        public BigInteger ballotId;

        public BigInteger proposalId;
    }

    public static class VoteRecordedEventResponse extends BaseEventResponse {
        public String voter;

        public BigInteger ballotId;

        public BigInteger proposalId;
    }
}
